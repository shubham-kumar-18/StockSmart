package com.stocksmart.report;

import com.stocksmart.inventory.*;
import com.stocksmart.order.*;
import com.stocksmart.security.CurrentWorkspace;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ReportController {
    private final InventoryService inventory;private final OrderService orders;private final StockMovementRepository movements;private final CurrentWorkspace workspace;
    public ReportController(InventoryService inventory,OrderService orders,StockMovementRepository movements,CurrentWorkspace workspace){this.inventory=inventory;this.orders=orders;this.movements=movements;this.workspace=workspace;}
    @GetMapping("/reports") public String reports(@RequestParam(required=false) LocalDate from,@RequestParam(required=false) LocalDate to,Authentication auth,Model model){
        Long org=workspace.organizationId(auth);var rows=inventory.all(org);var completed=orders.all(org).stream().filter(o->o.getStatus()==OrderStatus.FULFILLED).toList();
        BigDecimal valuation=rows.stream().filter(i->i.getProduct().getCostPrice()!=null).map(i->i.getQuantity().multiply(i.getProduct().getCostPrice())).reduce(BigDecimal.ZERO,BigDecimal::add);
        var fast=completed.stream().flatMap(o->o.getItems().stream()).collect(Collectors.groupingBy(i->i.getProductName()+" · "+i.getSku(),Collectors.reducing(BigDecimal.ZERO,OrderItem::getQuantity,BigDecimal::add))).entrySet().stream().sorted(Map.Entry.<String,BigDecimal>comparingByValue().reversed()).limit(10).toList();
        boolean incompleteRange=(from==null)!=(to==null);boolean invalidRange=from!=null&&to!=null&&to.isBefore(from);
        var movementRows=(from!=null&&to!=null&&!invalidRange)?movements.findAllByOrganization_IdAndCreatedAtBetweenOrderByCreatedAtDesc(org,from.atStartOfDay(),to.plusDays(1).atStartOfDay()):movements.recent(org,PageRequest.of(0,100));
        model.addAttribute("inventory",rows);model.addAttribute("valuation",valuation);model.addAttribute("costedRecordCount",rows.stream().filter(i->i.getProduct().getCostPrice()!=null).count());model.addAttribute("fulfilledOrders",completed.size());model.addAttribute("fastProducts",fast);model.addAttribute("movements",movementRows);model.addAttribute("from",from);model.addAttribute("to",to);model.addAttribute("dateError",invalidRange);model.addAttribute("incompleteRange",incompleteRange);
        return "reports";
    }
    @GetMapping("/reports/inventory.csv") public ResponseEntity<byte[]> inventoryCsv(Authentication auth){
        var rows=inventory.all(workspace.organizationId(auth));StringBuilder csv=new StringBuilder("Product,SKU,Location,On hand,Reserved,Available,Unit,Reorder level,Cost price,Inventory value\r\n");
        for(var row:rows){var p=row.getProduct();BigDecimal cost=p.getCostPrice();csv.append(cell(p.getName())).append(',').append(cell(p.getSku())).append(',').append(cell(row.getLocation().getName())).append(',').append(row.getQuantity()).append(',').append(row.getReservedQuantity()).append(',').append(row.getAvailableQuantity()).append(',').append(cell(p.getUnit())).append(',').append(p.getReorderLevel()).append(',').append(cost==null?"":cost).append(',').append(cost==null?"":row.getQuantity().multiply(cost)).append("\r\n");}
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/csv; charset=UTF-8")).header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=stocksmart-inventory.csv").body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }
    private String cell(String value){String s=value==null?"":value;if(!s.isEmpty()&&"=+-@".indexOf(s.charAt(0))>=0)s="'"+s;return "\""+s.replace("\"","\"\"")+"\"";}
}
