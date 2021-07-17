package org.jeff.pojo.vo;

public class ShopcartVO {
    private String itemId;
    private String itemImgUrl;
    private String itemName;
    private String specId;
    private String specName;
    private String priceDiscount;
    private String priceNormal;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemImgUrl() {
        return itemImgUrl;
    }

    public void setItemImgUrl(String itemImgUrl) {
        this.itemImgUrl = itemImgUrl;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getSpecId() {
        return specId;
    }

    public void setSpecId(String specId) {
        this.specId = specId;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public String getPriceDiscount() {
        return priceDiscount;
    }

    public void setPriceDiscount(String priceDiscount) {
        this.priceDiscount = priceDiscount;
    }

    public String getPriceNormal() {
        return priceNormal;
    }

    public void setPriceNormal(String priceNormal) {
        this.priceNormal = priceNormal;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ShopcartBO{");
        sb.append("itemId='").append(itemId).append('\'');
        sb.append(", itemImgUrl='").append(itemImgUrl).append('\'');
        sb.append(", itemName='").append(itemName).append('\'');
        sb.append(", specId='").append(specId).append('\'');
        sb.append(", specName='").append(specName).append('\'');
        sb.append(", priceDiscount='").append(priceDiscount).append('\'');
        sb.append(", priceNormal='").append(priceNormal).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
