package com.tapque.iapcn;

/**
 * @author Secret
 * @since 2019/4/18
 */
public class PurchaseBean implements Comparable<PurchaseBean> {

    private  boolean testProduct;//

    private  int purchaseType;//0: consume  1 unConsume

    private String purchaseItemName;//product name

    private double purchaseTotalPrice;//product price

    private double purchaseRealPrice;//product real price

    private String purchaseContent;// production content

    private int sequence;

    public void setPurchaseType(int purchaseType) {
        this.purchaseType = purchaseType;
    }

    public int getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseItemName(String purchaseItemName) {
        this.purchaseItemName = purchaseItemName;
    }


    public void setPurchaseTotalPrice(double purchaseTotalPrice) {
        this.purchaseTotalPrice = purchaseTotalPrice;
    }

    public void setPurchaseRealPrice(double purchaseRealPrice) {
        this.purchaseRealPrice = purchaseRealPrice;
    }


    public void setPurchaseContent(String purchaseContent) {
        this.purchaseContent = purchaseContent;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getPurchaseItemName() {
        return purchaseItemName;
    }
    public double getPurchaseTotalPrice() {
        return purchaseTotalPrice;
    }

    public double getPurchaseRealPrice() {
        return purchaseRealPrice;
    }


    public String getPurchaseContent() {
        return purchaseContent;
    }

    public int getSequence() {
        return sequence;
    }


    @Override
    public String toString() {
        return "PurchaseBean{" +
                "testProduct=" + testProduct +
                ", purchaseType=" + purchaseType +
                ", purchaseItemName='" + purchaseItemName + '\'' +
                ", purchaseTotalPrice=" + purchaseTotalPrice +
                ", purchaseRealPrice=" + purchaseRealPrice +
                ", purchaseContent='" + purchaseContent + '\'' +
                ", sequence=" + sequence +
                '}';
    }

    @Override
    public int compareTo(PurchaseBean o) {
        if (null == o) {
            return 0;
        }
        if (o.getSequence() > this.getSequence()) {
            return -1;
        } else {
            return 1;
        }
    }
}
