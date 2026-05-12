package com.erp.services.implemented;

import com.erp.dto.ConsumedBatchDTO;
import com.erp.enities.InventoryBatch;
import com.erp.enities.Product;
import com.erp.enities.ProductStock;
import com.erp.repositories.InventoryBatchRepository;
import com.erp.repositories.ProductStockRepository;
import com.erp.services.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImplement
        implements InventoryService {

    private final InventoryBatchRepository batchRepo;

    private final ProductStockRepository stockRepo;

    @Override
    @Transactional
    public List<ConsumedBatchDTO> consumeStock(Product product, Integer sellQty) {

        List<InventoryBatch> batches =
                batchRepo
                        .findByProductAndRemainingQuantityGreaterThanOrderByReceivedDateAscIdAsc(
                                product,
                                0
                        );

        int remaining = sellQty;

        List<ConsumedBatchDTO> consumedList = new ArrayList<>();

        for (InventoryBatch batch : batches) {

            if (remaining <= 0) {
                break;
            }

            int available = batch.getRemainingQuantity();

            // how much consume from this batch
            int consumeQty = Math.min(available, remaining);

            // reduce batch qty
            batch.setRemainingQuantity(
                    available - consumeQty
            );

            batchRepo.save(batch);

            consumedList.add(
                    new ConsumedBatchDTO(batch, consumeQty)
            );

            remaining = remaining - consumeQty;
        }

        // stock shortage
        if (remaining > 0) {
            throw new RuntimeException(
                    "Insufficient stock for product: "
                            + product.getName()
            );
        }

        // total stock reduce
        ProductStock stock =
                stockRepo.findByProduct(product)
                        .orElseThrow(() ->
                                new RuntimeException("Stock not found")
                        );

        stock.setQuantity(
                stock.getQuantity() - sellQty
        );

        stockRepo.save(stock);

        return consumedList;
    }
}