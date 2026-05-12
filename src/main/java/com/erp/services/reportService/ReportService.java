package com.erp.services.reportService;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {
    final private DataSource dataSource;

    public byte[] generateReport(String reportName, Map<String, Object> params) throws IOException, JRException, SQLException {
        //To read jasper file
        InputStream inputStream = new ClassPathResource("report/"+reportName+".jasper").getInputStream();
        //Load the input stream in Jasper Object
        JasperReport jr = (JasperReport)  JRLoader.loadObject(inputStream);

        //get the connection of databse
        Connection con = dataSource.getConnection();
        //Prepare the Report to print
        JasperPrint jasperPrint = JasperFillManager.fillReport(jr,params,con);
        con.close();
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}