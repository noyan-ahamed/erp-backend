package com.erp.services.implemented;

import com.erp.dto.admin_dashboard.*;
import com.erp.enities.*;
import com.erp.enums.CustomerPaymentStatus;
import com.erp.enums.PurchaseStatus;
import com.erp.enums.SalesStatus;
import com.erp.repositories.*;
import com.erp.services.CustomerLedgerService;
import com.erp.services.DashboardService;
import com.erp.services.SupplierLedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImplement implements DashboardService {

    private final SalesOrderHeaderRepository salesOrderHeaderRepository;
    private final CustomerPaymentRepository customerPaymentRepository;
    private final SupplierPaymentRepository supplierPaymentRepository;
    private final ProductRepository productRepository;
    private final PurchaseOrderHeaderRepository purchaseOrderHeaderRepository;
    private final CustomerLedgerService customerLedgerService;
    private final SupplierLedgerService supplierLedgerService;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Override
    public DashboardResponseDTO getDashboardData(String filterType, LocalDate fromDate, LocalDate toDate) {

        LocalDate today = LocalDate.now();

        // ---------- Resolve Date Range ----------
        LocalDate startDate;
        LocalDate endDate;

        switch (filterType != null ? filterType.toUpperCase() : "MONTH") {
            case "TODAY" -> {
                startDate = today;
                endDate = today;
            }
            case "WEEK" -> {
                startDate = today.minusDays(6);
                endDate = today;
            }
            case "YEAR" -> {
                startDate = today.withDayOfYear(1);
                endDate = today;
            }
            case "CUSTOM" -> {
                startDate = fromDate != null ? fromDate : today.minusDays(29);
                endDate = toDate != null ? toDate : today;
            }
            default -> {
                // MONTH default => last 30 days
                startDate = today.minusDays(29);
                endDate = today;
            }
        }

        DashboardResponseDTO response = new DashboardResponseDTO();

        response.setSummary(buildSummary(today, startDate, endDate));
        response.setSalesProfitTrend(buildSalesProfitTrend(startDate, endDate));
        response.setMonthlySalesComparison(buildMonthlySalesComparison(today.getYear()));
        response.setPaymentMethodDistribution(buildPaymentMethodDistribution(startDate, endDate));
        response.setLowStockItems(buildLowStockItems());
        response.setRecentActivities(buildRecentActivities());
        response.setTopCustomers(buildTopCustomers(startDate, endDate));
        response.setTopProducts(buildTopProducts(startDate, endDate));

        return response;
    }

    // =========================================================
    // SUMMARY
    // =========================================================
    private DashboardSummaryDTO buildSummary(LocalDate today, LocalDate startDate, LocalDate endDate) {
        DashboardSummaryDTO dto = new DashboardSummaryDTO();

        LocalDate monthStart = today.withDayOfMonth(1);

        List<SalesOrderHeader> allSales = salesOrderHeaderRepository.findAll();
        List<PurchaseOrderHeader> allPurchases = purchaseOrderHeaderRepository.findAll();

        // ONLY VALID SALES
        List<SalesOrderHeader> validSales = allSales.stream()
                .filter(Objects::nonNull)
                .filter(s -> s.getSalesDate() != null)
                .filter(s -> s.getStatus() != null) // IMPORTANT
                .filter(s ->
                        s.getStatus() == SalesStatus.COMPLETED ||
                                s.getStatus() == SalesStatus.DRAFT ||
                                s.getStatus() == SalesStatus.CANCELLED
                )
                .toList();

        BigDecimal todaySales = validSales.stream()
                .filter(s -> s.getSalesDate().isEqual(today))
                .map(s -> safe(s.getNetTotal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal monthSales = validSales.stream()
                .filter(s -> !s.getSalesDate().isBefore(monthStart) &&
                        !s.getSalesDate().isAfter(today))
                .map(s -> safe(s.getNetTotal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal todayProfit = calculateProfitForRange(today, today);
        BigDecimal monthProfit = calculateProfitForRange(monthStart, today);

        BigDecimal totalCustomerDue = customerLedgerService.getAllCustomerDueSummary().stream()
                .map(x -> safe(x.getCurrentDue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSupplierPayable = supplierLedgerService.getAllSupplierDueSummaries().stream()
                .map(x -> safe(x.getCurrentDue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long lowStockCount = productRepository.findAll().stream()
                .filter(p -> p.getProductStock() != null)
                .filter(p -> p.getProductStock().getQuantity() != null
                        && p.getMinStockLevel() != null)
                .filter(p -> p.getProductStock().getQuantity()
                        .compareTo(Integer.valueOf(p.getMinStockLevel())) <= 0)
                .count();

        long totalProducts = productRepository.count();

        dto.setTodaySales(todaySales);
        dto.setMonthSales(monthSales);
        dto.setTodayProfit(todayProfit);
        dto.setMonthProfit(monthProfit);
        dto.setTotalCustomerDue(totalCustomerDue);
        dto.setTotalSupplierPayable(totalSupplierPayable);
        dto.setLowStockCount(lowStockCount);
        dto.setTotalProducts(totalProducts);

        return dto;
    }


    // =========================================================
    // SALES / PROFIT TREND
    // =========================================================
    private List<DashboardTrendPointDTO> buildSalesProfitTrend(LocalDate startDate, LocalDate endDate) {
        List<DashboardTrendPointDTO> list = new ArrayList<>();

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            final LocalDate day = current; // <-- effectively final

            DashboardTrendPointDTO dto = new DashboardTrendPointDTO();
            dto.setLabel(day.toString());

            BigDecimal daySales = salesOrderHeaderRepository.findAll().stream()
                    .filter(s -> s.getSalesDate() != null && s.getSalesDate().isEqual(day))
                    .map(s -> safe(s.getNetTotal()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal dayProfit = calculateProfitForRange(day, day);

            dto.setSales(daySales);
            dto.setProfit(dayProfit);

            list.add(dto);
            current = current.plusDays(1);
        }

        return list;
    }

    // =========================================================
    // MONTHLY SALES COMPARISON
    // =========================================================
    private List<DashboardTrendPointDTO> buildMonthlySalesComparison(int year) {
        List<DashboardTrendPointDTO> list = new ArrayList<>();

        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        List<SalesOrderHeader> sales = salesOrderHeaderRepository.findAll();

        for (int i = 1; i <= 12; i++) {
            int month = i;

            BigDecimal totalSales = sales.stream()
                    .filter(s -> s.getSalesDate() != null)
                    .filter(s -> s.getSalesDate().getYear() == year)
                    .filter(s -> s.getSalesDate().getMonthValue() == month)
                    .map(s -> safe(s.getNetTotal()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalProfit = calculateProfitForMonth(year, month);

            DashboardTrendPointDTO dto = new DashboardTrendPointDTO();
            dto.setLabel(months[i - 1]);
            dto.setSales(totalSales);
            dto.setProfit(totalProfit);

            list.add(dto);
        }

        return list;
    }

    // =========================================================
    // PAYMENT METHOD DISTRIBUTION
    // =========================================================
    private List<DashboardPaymentMethodDTO> buildPaymentMethodDistribution(LocalDate startDate, LocalDate endDate) {
        Map<String, BigDecimal> grouped = new LinkedHashMap<>();

        customerPaymentRepository.findAll().stream()
                .filter(p -> p.getPaymentDate() != null)
                .filter(p -> !p.getPaymentDate().isBefore(startDate) && !p.getPaymentDate().isAfter(endDate))
                .filter(p -> p.getStatus() == CustomerPaymentStatus.APPROVED)
                .forEach(p -> {
                    String key = p.getPaymentMethod() != null ? p.getPaymentMethod().name() : "UNKNOWN";
                    grouped.put(key, grouped.getOrDefault(key, BigDecimal.ZERO).add(safe(p.getAmount())));
                });

        List<DashboardPaymentMethodDTO> list = new ArrayList<>();

        for (Map.Entry<String, BigDecimal> entry : grouped.entrySet()) {
            DashboardPaymentMethodDTO dto = new DashboardPaymentMethodDTO();
            dto.setPaymentMethod(entry.getKey());
            dto.setTotalAmount(entry.getValue());
            list.add(dto);
        }

        return list;
    }

    // =========================================================
    // LOW STOCK
    // =========================================================
    private List<DashboardLowStockItemDTO> buildLowStockItems() {
        return productRepository.findAll().stream()
                .filter(p -> p.getProductStock() != null)
                .filter(p -> p.getProductStock().getQuantity() != null
                        && p.getMinStockLevel() != null)
                .filter(p -> p.getProductStock().getQuantity()
                        .compareTo(Integer.valueOf(p.getMinStockLevel())) <= 0)
                .sorted(Comparator.comparing(
                        p -> p.getProductStock().getQuantity()
                ))
                .limit(10)
                .map(p -> {
                    DashboardLowStockItemDTO dto = new DashboardLowStockItemDTO();
                    dto.setProductId(p.getId());
                    dto.setProductName(p.getName());
                    dto.setSku(p.getSku());
                    dto.setStock(p.getProductStock().getQuantity());
                    dto.setMinStockLevel(Integer.valueOf(p.getMinStockLevel()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // =========================================================
    // RECENT ACTIVITIES
    // =========================================================
    private List<DashboardActivityDTO> buildRecentActivities() {
        List<DashboardActivityDTO> list = new ArrayList<>();

        salesOrderHeaderRepository.findAll().stream()
                .sorted(Comparator.comparing(SalesOrderHeader::getId).reversed())
                .limit(5)
                .forEach(sale -> {
                    DashboardActivityDTO dto = new DashboardActivityDTO();
                    dto.setType("SALE");
                    dto.setTitle("Sale - " + sale.getInvoiceNumber());
                    dto.setAmount(safe(sale.getNetTotal()));
                    dto.setDate(sale.getSalesDate());
                    list.add(dto);
                });

        purchaseOrderHeaderRepository.findAll().stream()
                .sorted(Comparator.comparing(PurchaseOrderHeader::getId).reversed())
                .limit(5)
                .forEach(purchase -> {
                    DashboardActivityDTO dto = new DashboardActivityDTO();
                    dto.setType("PURCHASE");
                    dto.setTitle("Purchase - " + purchase.getInvoiceNumber());
                    dto.setAmount(safe(purchase.getTotalAmount()));
                    dto.setDate(purchase.getCreated_at());
                    list.add(dto);
                });

        customerPaymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == CustomerPaymentStatus.APPROVED)
                .sorted(Comparator.comparing(CustomerPayment::getId).reversed())
                .limit(5)
                .forEach(payment -> {
                    DashboardActivityDTO dto = new DashboardActivityDTO();
                    dto.setType("CUSTOMER_PAYMENT");
                    dto.setTitle("Customer Payment - " + payment.getVoucherNo());
                    dto.setAmount(safe(payment.getAmount()));
                    dto.setDate(payment.getPaymentDate());
                    list.add(dto);
                });

        supplierPaymentRepository.findAll().stream()
                .sorted(Comparator.comparing(SupplierPayment::getId).reversed())
                .limit(5)
                .forEach(payment -> {
                    DashboardActivityDTO dto = new DashboardActivityDTO();
                    dto.setType("SUPPLIER_PAYMENT");
                    dto.setTitle("Supplier Payment - " + payment.getVoucherNo());
                    dto.setAmount(safe(payment.getAmount()));
                    dto.setDate(payment.getPaymentDate());
                    list.add(dto);
                });

        return list.stream()
                .sorted(Comparator.comparing(DashboardActivityDTO::getDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10)
                .collect(Collectors.toList());
    }

    // =========================================================
    // TOP CUSTOMERS
    // =========================================================
    private List<DashboardTopCustomerDTO> buildTopCustomers(LocalDate startDate, LocalDate endDate) {
        Map<Long, DashboardTopCustomerDTO> map = new HashMap<>();

        salesOrderHeaderRepository.findAll().stream()
                .filter(s -> s.getSalesDate() != null)
                .filter(s -> !s.getSalesDate().isBefore(startDate) && !s.getSalesDate().isAfter(endDate))
                .forEach(sale -> {
                    if (sale.getCustomer() == null) return;

                    Long customerId = sale.getCustomer().getId();

                    map.putIfAbsent(customerId, new DashboardTopCustomerDTO());
                    DashboardTopCustomerDTO dto = map.get(customerId);

                    dto.setCustomerId(customerId);
                    dto.setCustomerName(sale.getCustomer().getName());
                    dto.setMobileNumber(sale.getCustomer().getMobileNumber());
                    dto.setTotalPurchaseAmount(safe(dto.getTotalPurchaseAmount()).add(safe(sale.getNetTotal())));
                });

        return map.values().stream()
                .sorted(Comparator.comparing(DashboardTopCustomerDTO::getTotalPurchaseAmount).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    // =========================================================
    // TOP PRODUCTS
    // =========================================================
    private List<DashboardTopProductDTO> buildTopProducts(LocalDate startDate, LocalDate endDate) {
        Map<Long, DashboardTopProductDTO> map = new HashMap<>();

        salesOrderHeaderRepository.findAll().stream()
                .filter(s -> s.getSalesDate() != null)
                .filter(s -> !s.getSalesDate().isBefore(startDate) && !s.getSalesDate().isAfter(endDate))
                .forEach(order -> {
                    if (order.getItems() == null) return;

                    order.getItems().forEach(item -> {
                        if (item.getProduct() == null) return;

                        Long productId = item.getProduct().getId();

                        map.putIfAbsent(productId, new DashboardTopProductDTO());
                        DashboardTopProductDTO dto = map.get(productId);

                        dto.setProductId(productId);
                        dto.setProductName(item.getProduct().getName());
                        dto.setSku(item.getProduct().getSku());

//                        Integer qty = item.getQuantity() != null ? Integer.valueOf(item.getQuantity()) : 0;
                        BigDecimal amount = safe(item.getLineTotal());

                        Integer qty = item.getQuantity() != null ? item.getQuantity() : 0;

                        dto.setTotalSoldQty(
                                (dto.getTotalSoldQty() != null ? dto.getTotalSoldQty() : 0) + qty
                        );
                        dto.setTotalSoldAmount(safe(dto.getTotalSoldAmount()).add(amount));
                    });
                });

        return map.values().stream()
                .sorted(Comparator.comparing(DashboardTopProductDTO::getTotalSoldAmount).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    // =========================================================
    // PROFIT CALCULATION
    // =========================================================
    private BigDecimal calculateProfitForRange(LocalDate startDate, LocalDate endDate) {
        return salesOrderHeaderRepository.findAll().stream()
                .filter(Objects::nonNull)
                .filter(s -> s.getSalesDate() != null)
                .filter(s -> s.getStatus() != null)
                .filter(s ->
                        s.getStatus() == SalesStatus.COMPLETED ||
                                s.getStatus() == SalesStatus.DRAFT ||
                                s.getStatus() == SalesStatus.CANCELLED
                )
                .filter(s -> !s.getSalesDate().isBefore(startDate) && !s.getSalesDate().isAfter(endDate))
                .map(this::calculateProfitFromOrder)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateProfitForMonth(int year, int month) {
        return salesOrderHeaderRepository.findAll().stream()
                .filter(Objects::nonNull)
                .filter(s -> s.getSalesDate() != null)
                .filter(s -> s.getStatus() != null)
                .filter(s ->
                        s.getStatus() == SalesStatus.COMPLETED ||
                                s.getStatus() == SalesStatus.DRAFT ||
                                s.getStatus() == SalesStatus.CANCELLED
                )
                .filter(s -> s.getSalesDate().getYear() == year)
                .filter(s -> s.getSalesDate().getMonthValue() == month)
                .map(this::calculateProfitFromOrder)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateProfitFromOrder(SalesOrderHeader order) {

        if (order.getItems() == null || order.getItems().isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal profit = BigDecimal.ZERO;

        for (var item : order.getItems()) {

            BigDecimal saleAmount = safe(item.getLineTotal());
            BigDecimal purchaseCost = BigDecimal.ZERO;

            if (item.getProduct() != null && item.getQuantity() != null) {

                List<PurchaseOrderItem> list =
                        purchaseOrderItemRepository.findLatestPurchase(
                                item.getProduct().getId(),
                                PageRequest.of(0, 1)
                        );

                if (!list.isEmpty() && list.get(0).getUnitPrice() != null) {

                    BigDecimal unitPrice = list.get(0).getUnitPrice();

                    purchaseCost = unitPrice.multiply(
                            BigDecimal.valueOf(item.getQuantity())
                    );
                }
            }

            profit = profit.add(saleAmount.subtract(purchaseCost));
        }

        return profit;
    }

    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
