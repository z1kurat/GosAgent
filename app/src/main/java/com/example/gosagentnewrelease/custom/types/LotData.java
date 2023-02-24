package com.example.gosagentnewrelease.custom.types;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LotData extends MarkerData {
    public List<String> description = new ArrayList<>();
    public List<String> imageLinks = new ArrayList<>();
    public List<String> lotAttachmentsLinks = new ArrayList<>();
    public String number;
    public String link;
    public String location;
    public double initialPrice;
    public double monthlyPayment;
    public String deposit;
    public int subjectRFCode;
    public String category;
    public String type;
    public Date bidStartTime;
    public Date bidEndTime;


    public LotData(List<String> description) {
        this.description.addAll(description);
    }

    public LotData(double coordinatesX, double coordinatesY) {
        super(coordinatesX, coordinatesY);
    }

    public LotData() {
        super();
    }

    public LotData(String numberLot, String linkLot, String locationLot, Double initialPriceLot, List<String> imageLinks, Double monthlyPaymentLot, List<String> lotConfigs, String deposit, int subjectRFCode, Date biddStartTime, Date biddEndTime, List<String> lotAttachmentsLinks, String category) {
        this.number = numberLot;
        this.link = linkLot;
        this.location = locationLot;
        this.initialPrice = initialPriceLot;
        this.imageLinks = imageLinks;
        this.monthlyPayment = monthlyPaymentLot;
        this.description.addAll(lotConfigs);

        this.deposit = deposit;
        this.subjectRFCode = subjectRFCode;
        this.category = category;
        this.bidStartTime = biddStartTime;
        this.bidEndTime = biddEndTime;
        this.lotAttachmentsLinks.addAll(lotAttachmentsLinks);
    }

}
