package com.acme.accountingsrv.invoice;

import java.time.Instant;
import java.util.UUID;

public class Invoice {
    private UUID id;
    private UUID companyId;
    private int year;
    private int month; // Jan = 1
    private InvoiceStatus status;
    private Instant createdDate;
    private Instant processedDate;
    private String errorText;
}
