package com.shadow.gmall.beans;


import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
//通过网络传输对象，对象必须序列化
public class UmsMemberReceiveAddress implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String memberId;
    private String name;
    private String phoneNumber;
    private String defaultStatus;
    private String postCode;
    private String province;
    private String city;
    private String region;
    private String detailAddress;
    private UmsMember umsMembers;

    public UmsMemberReceiveAddress(String id, String memberId, String name, String phoneNumber, String defaultStatus, String postCode, String province, String city, String region, String detailAddress, UmsMember umsMembers) {
        this.id = id;
        this.memberId = memberId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.defaultStatus = defaultStatus;
        this.postCode = postCode;
        this.province = province;
        this.city = city;
        this.region = region;
        this.detailAddress = detailAddress;
        this.umsMembers = umsMembers;
    }


    public UmsMemberReceiveAddress() {
    }

    public UmsMember getUmsMembers() {
        return umsMembers;
    }

    public void setUmsMembers(UmsMember umsMembers) {
        this.umsMembers = umsMembers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(String defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    @Override
    public String toString() {
        return "UmsMemberReceiveAddress{" +
                "id='" + id + '\'' +
                ", memberId='" + memberId + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", defaultStatus='" + defaultStatus + '\'' +
                ", postCode='" + postCode + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", region='" + region + '\'' +
                ", detailAddress='" + detailAddress + '\'' +
                ", umsMembers=" + umsMembers +
                '}';
    }
}
