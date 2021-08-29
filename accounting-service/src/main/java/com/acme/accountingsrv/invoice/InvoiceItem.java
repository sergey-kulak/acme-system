package com.acme.accountingsrv.invoice;

import java.math.BigDecimal;
import java.util.UUID;

public class InvoiceItem {
    private UUID id;
    private UUID invoiceId;
    private UUID companyPlanId;
    private BigDecimal cost;
}
