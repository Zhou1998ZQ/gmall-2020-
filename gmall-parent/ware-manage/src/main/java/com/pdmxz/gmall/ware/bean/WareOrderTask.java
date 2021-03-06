package com.pdmxz.gmall.ware.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.Date;
import java.util.List;

/**
 * {
 *      "orderBody":"华为 HUAWEI Mate 30 5G 麒麟990 4000万超感光徕卡影像双超级快充8GB+128GB亮黑色5G全网通游戏手机 Redmi K30 5G双模 120Hz流速屏 骁龙765G 30W快充 6GB+128GB 深海微光 游戏智能手机 小米 红米 ",
 *      "consignee":"admin",
 *      "orderComment":"",
 *      "orderId":1,"
 *      consigneeTel":"17800000",
 *      "deliveryAddress":"北京市昌平区2",
 *      "details":[{
 *              "skuName":"华为 HUAWEI Mate 30 5G 麒麟990 4000万超感光徕卡影像双超级快充8GB+128GB亮黑色5G全网通游戏手机",
 *              "skuId":10,
 *              "skuNum":2
 *              },{
 *              "skuName":"Redmi K30 5G双模 120Hz流速屏 骁龙765G 30W快充 6GB+128GB 深海微光 游戏智能手机 小米 红米","skuId":14,"skuNum":5}],"paymentWay":"2"}
 * @param
 * @return
 */
public class WareOrderTask {

    @TableId(type = IdType.AUTO)
    private String id ;

    @TableField
    private String orderId;

    @TableField
    private String consignee;

    @TableField
    private String consigneeTel;

    @TableField
    private String deliveryAddress;

    @TableField
    private String orderComment;

    @TableField
    private String paymentWay;

    @TableField
    private String taskStatus;

    @TableField
    private String orderBody;

    @TableField
    private String trackingNo;

    @TableField
    private Date createTime;

    @TableField
    private String wareId;

    @TableField
    private String taskComment;

    @TableField(exist = false)
    private List<com.pdmxz.gmall.ware.bean.WareOrderTaskDetail> details;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getConsigneeTel() {
        return consigneeTel;
    }

    public void setConsigneeTel(String consigneeTel) {
        this.consigneeTel = consigneeTel;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getOrderComment() {
        return orderComment;
    }

    public void setOrderComment(String orderComment) {
        this.orderComment = orderComment;
    }

    public String getPaymentWay() {
        return paymentWay;
    }

    public void setPaymentWay(String paymentWay) {
        this.paymentWay = paymentWay;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getOrderBody() {
        return orderBody;
    }

    public void setOrderBody(String orderBody) {
        this.orderBody = orderBody;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<com.pdmxz.gmall.ware.bean.WareOrderTaskDetail> getDetails() {
        return details;
    }



    public void setDetails(List<com.pdmxz.gmall.ware.bean.WareOrderTaskDetail> details) {
        this.details = details;
    }

    public String getWareId() {
        return wareId;
    }

    public void setWareId(String wareId) {
        this.wareId = wareId;
    }

    public String getTaskComment() {
        return taskComment;
    }

    public void setTaskComment(String taskComment) {
        this.taskComment = taskComment;
    }
}
