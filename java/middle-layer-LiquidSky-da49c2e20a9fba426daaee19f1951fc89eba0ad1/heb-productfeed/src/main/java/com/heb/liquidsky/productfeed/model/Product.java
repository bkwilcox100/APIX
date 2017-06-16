
package com.heb.liquidsky.productfeed.model;

import java.util.List;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Product {

	@Id private Long prodId;
    private Long upc;
    private Boolean bonusPack;
    private Boolean plu;
    private String type;
    private Boolean showOnSite;
    private Double averageWeight;
    private Boolean privateLabel;
    private Boolean weightTolerance;
    private Long minOrderValue;
    private Long maxOrderValue;
    private Double orderIncrement;
    private Boolean isTaxable;
    private String brand;
    private Long classCode;
    private String departmentCode;
    private String displayName;
    private String description;
    private String longDescription;
    private String startDate;
    private String endDate;
    private List<Long> childSku = null;
    private String pssDepartmentId;
    private String pssDepartmentName;
    private String salesChannel;
    private Double alcoholPct;
    private Boolean primoPick;
    private Boolean ownBrand;

    private Boolean defaultparentCategory;
    private Long defaultSku;
    private String template;
    private Boolean fsa;
    private Boolean wic;
    private Boolean leb;
    private Boolean foodStamp;
    private Boolean heartHealthyClaim;
    private Boolean lowSodiumClaim;
    private Boolean fiberSourceClaim;
    private Boolean lowSaturatedFatClaim;
    private Boolean madeInTexas;
    private Boolean isAddOnProduct;
    private String taxCategoryCD;
    private Boolean ageRestriction;
    private String subdepartmentCode;
    private Boolean isLargeSurcharge;
    private String customerFriendlySize;
    private String fulfillmentChnl;
    private Double retlUntWd;
    private Double retlUntLn;
    private Double retlUntHt;
    private Double retlUntWt;
    private String stdUomCd;
    private String uomQty;
    private Long imgUri;
    private Long minAgeRestriction;

    private Boolean isSupplement;
    private List<ProductFulfillmentChnl> productFulfillmentChnl = null;

    private List<Category> categories = null;
    private List<ProdExtAttrib> prodExtAttrib = null;

    public Long getProdId() {
        return prodId;
    }

    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    public Long getUpc() {
        return upc;
    }

    public void setUpc(Long upc) {
        this.upc = upc;
    }

    public Boolean getBonusPack() {
        return bonusPack;
    }

    public void setBonusPack(Boolean bonusPack) {
        this.bonusPack = bonusPack;
    }

    public Boolean getPlu() {
        return plu;
    }

    public void setPlu(Boolean plu) {
        this.plu = plu;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getShowOnSite() {
        return showOnSite;
    }

    public void setShowOnSite(Boolean showOnSite) {
        this.showOnSite = showOnSite;
    }

    public Double getAverageWeight() {
        return averageWeight;
    }

    public void setAverageWeight(Double averageWeight) {
        this.averageWeight = averageWeight;
    }

    public Boolean getPrivateLabel() {
        return privateLabel;
    }

    public void setPrivateLabel(Boolean privateLabel) {
        this.privateLabel = privateLabel;
    }

    public Boolean getWeightTolerance() {
        return weightTolerance;
    }

    public void setWeightTolerance(Boolean weightTolerance) {
        this.weightTolerance = weightTolerance;
    }

    public Long getMinOrderValue() {
        return minOrderValue;
    }

    public void setMinOrderValue(Long minOrderValue) {
        this.minOrderValue = minOrderValue;
    }

    public Long getMaxOrderValue() {
        return maxOrderValue;
    }

    public void setMaxOrderValue(Long maxOrderValue) {
        this.maxOrderValue = maxOrderValue;
    }

    public Double getOrderIncrement() {
        return orderIncrement;
    }

    public void setOrderIncrement(Double orderIncrement) {
        this.orderIncrement = orderIncrement;
    }

    public Boolean getIsTaxable() {
        return isTaxable;
    }

    public void setIsTaxable(Boolean isTaxable) {
        this.isTaxable = isTaxable;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Long getClassCode() {
        return classCode;
    }

    public void setClassCode(Long classCode) {
        this.classCode = classCode;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<Long> getChildSku() {
        return childSku;
    }

    public void setChildSku(List<Long> childSku) {
        this.childSku = childSku;
    }

    public String getPssDepartmentId() {
        return pssDepartmentId;
    }

    public void setPssDepartmentId(String pssDepartmentId) {
        this.pssDepartmentId = pssDepartmentId;
    }

    public String getPssDepartmentName() {
        return pssDepartmentName;
    }

    public void setPssDepartmentName(String pssDepartmentName) {
        this.pssDepartmentName = pssDepartmentName;
    }

    public String getSalesChannel() {
        return salesChannel;
    }

    public void setSalesChannel(String salesChannel) {
        this.salesChannel = salesChannel;
    }

    public Double getAlcoholPct() {
        return alcoholPct;
    }

    public void setAlcoholPct(Double alcoholPct) {
        this.alcoholPct = alcoholPct;
    }

    public Boolean getPrimoPick() {
        return primoPick;
    }

    public void setPrimoPick(Boolean primoPick) {
        this.primoPick = primoPick;
    }

    public Boolean getOwnBrand() {
        return ownBrand;
    }

    public void setOwnBrand(Boolean ownBrand) {
        this.ownBrand = ownBrand;
    }

    public Boolean getDefaultparentCategory() {
        return defaultparentCategory;
    }

    public void setDefaultparentCategory(Boolean defaultparentCategory) {
        this.defaultparentCategory = defaultparentCategory;
    }

    public Long getDefaultSku() {
        return defaultSku;
    }

    public void setDefaultSku(Long defaultSku) {
        this.defaultSku = defaultSku;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Boolean getFsa() {
        return fsa;
    }

    public void setFsa(Boolean fsa) {
        this.fsa = fsa;
    }

    public Boolean getWic() {
        return wic;
    }

    public void setWic(Boolean wic) {
        this.wic = wic;
    }

    public Boolean getLeb() {
        return leb;
    }

    public void setLeb(Boolean leb) {
        this.leb = leb;
    }

    public Boolean getFoodStamp() {
        return foodStamp;
    }

    public void setFoodStamp(Boolean foodStamp) {
        this.foodStamp = foodStamp;
    }

    public Boolean getHeartHealthyClaim() {
        return heartHealthyClaim;
    }

    public void setHeartHealthyClaim(Boolean heartHealthyClaim) {
        this.heartHealthyClaim = heartHealthyClaim;
    }

    public Boolean getLowSodiumClaim() {
        return lowSodiumClaim;
    }

    public void setLowSodiumClaim(Boolean lowSodiumClaim) {
        this.lowSodiumClaim = lowSodiumClaim;
    }

    public Boolean getFiberSourceClaim() {
        return fiberSourceClaim;
    }

    public void setFiberSourceClaim(Boolean fiberSourceClaim) {
        this.fiberSourceClaim = fiberSourceClaim;
    }

    public Boolean getLowSaturatedFatClaim() {
        return lowSaturatedFatClaim;
    }

    public void setLowSaturatedFatClaim(Boolean lowSaturatedFatClaim) {
        this.lowSaturatedFatClaim = lowSaturatedFatClaim;
    }

    public Boolean getMadeInTexas() {
        return madeInTexas;
    }

    public void setMadeInTexas(Boolean madeInTexas) {
        this.madeInTexas = madeInTexas;
    }

    public Boolean getIsAddOnProduct() {
        return isAddOnProduct;
    }

    public void setIsAddOnProduct(Boolean isAddOnProduct) {
        this.isAddOnProduct = isAddOnProduct;
    }

    public String getTaxCategoryCD() {
        return taxCategoryCD;
    }

    public void setTaxCategoryCD(String taxCategoryCD) {
        this.taxCategoryCD = taxCategoryCD;
    }

    public Boolean getAgeRestriction() {
        return ageRestriction;
    }

    public void setAgeRestriction(Boolean ageRestriction) {
        this.ageRestriction = ageRestriction;
    }

    public String getSubdepartmentCode() {
        return subdepartmentCode;
    }

    public void setSubdepartmentCode(String subdepartmentCode) {
        this.subdepartmentCode = subdepartmentCode;
    }

    public Boolean getIsLargeSurcharge() {
        return isLargeSurcharge;
    }

    public void setIsLargeSurcharge(Boolean isLargeSurcharge) {
        this.isLargeSurcharge = isLargeSurcharge;
    }

    public String getCustomerFriendlySize() {
        return customerFriendlySize;
    }

    public void setCustomerFriendlySize(String customerFriendlySize) {
        this.customerFriendlySize = customerFriendlySize;
    }

    public String getFulfillmentChnl() {
        return fulfillmentChnl;
    }

    public void setFulfillmentChnl(String fulfillmentChnl) {
        this.fulfillmentChnl = fulfillmentChnl;
    }

    public Double getRetlUntWd() {
        return retlUntWd;
    }

    public void setRetlUntWd(Double retlUntWd) {
        this.retlUntWd = retlUntWd;
    }

    public Double getRetlUntLn() {
        return retlUntLn;
    }

    public void setRetlUntLn(Double retlUntLn) {
        this.retlUntLn = retlUntLn;
    }

    public Double getRetlUntHt() {
        return retlUntHt;
    }

    public void setRetlUntHt(Double retlUntHt) {
        this.retlUntHt = retlUntHt;
    }

    public Double getRetlUntWt() {
        return retlUntWt;
    }

    public void setRetlUntWt(Double retlUntWt) {
        this.retlUntWt = retlUntWt;
    }

    public String getStdUomCd() {
        return stdUomCd;
    }

    public void setStdUomCd(String stdUomCd) {
        this.stdUomCd = stdUomCd;
    }

    public String getUomQty() {
        return uomQty;
    }

    public void setUomQty(String uomQty) {
        this.uomQty = uomQty;
    }

    public Long getImgUri() {
        return imgUri;
    }

    public void setImgUri(Long imgUri) {
        this.imgUri = imgUri;
    }

    public Long getMinAgeRestriction() {
        return minAgeRestriction;
    }

    public void setMinAgeRestriction(Long minAgeRestriction) {
        this.minAgeRestriction = minAgeRestriction;
    }

    public Boolean getIsSupplement() {
        return isSupplement;
    }

    public void setIsSupplement(Boolean isSupplement) {
        this.isSupplement = isSupplement;
    }

    public List<ProductFulfillmentChnl> getProductFulfillmentChnl() {
        return productFulfillmentChnl;
    }

    public void setProductFulfillmentChnl(List<ProductFulfillmentChnl> productFulfillmentChnl) {
        this.productFulfillmentChnl = productFulfillmentChnl;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<ProdExtAttrib> getProdExtAttrib() {
        return prodExtAttrib;
    }

    public void setProdExtAttrib(List<ProdExtAttrib> prodExtAttrib) {
        this.prodExtAttrib = prodExtAttrib;
    }

}
