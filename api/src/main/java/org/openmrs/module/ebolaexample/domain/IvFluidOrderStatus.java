package org.openmrs.module.ebolaexample.domain;

import org.openmrs.OpenmrsObject;
import org.openmrs.User;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "ebola_ivfluid_order_status")
public class IvFluidOrderStatus implements OpenmrsObject {

    public IvFluidOrderStatus() {
    }

    public static enum IVFluidOrderStatus{
        NOT_STARTED, STARTED, RESTARTED, HELD, FINISHED
    }

    public IvFluidOrderStatus(IvFluidOrder order, IVFluidOrderStatus status) {
        this.ivFluidOrder = order;
        this.status = status;
        this.dateCreated = this.dateChanged = new Date();
    }

    @Id
    @GeneratedValue
    @Column(name = "order_status_id")
    private Integer orderStatusId;

    @Basic
    @Column(name = "uuid", length = 38, unique = true)
    private String uuid  = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name="order_id")
    private IvFluidOrder ivFluidOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private IVFluidOrderStatus status;

    @ManyToOne
    @JoinColumn(name = "creator")
    protected User creator;

    @Column(name = "voided", nullable = false, columnDefinition = "boolean default false")
    private Boolean voided = false;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_voided" )
    private Date dateVoided;

    @ManyToOne
    @JoinColumn(name = "voided_by")
    private User voidedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created" )
    private Date dateCreated;

    @ManyToOne
    @JoinColumn(name = "changed_by")
    private User changedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_changed" )
    private Date dateChanged;

    public IVFluidOrderStatus getStatus(){
        return status;
    }


    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }

    public Date getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(Date dateChanged) {
        this.dateChanged = dateChanged;
    }

    public Boolean isVoided() {
        return voided;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    public User getVoidedBy() {
        return voidedBy;
    }

    public void setVoidedBy(User voidedBy) {
        this.voidedBy = voidedBy;
    }

    public Date getDateVoided() {
        return dateVoided;
    }

    public void setDateVoided(Date dateVoided) {
        this.dateVoided = dateVoided;
    }

    public IvFluidOrder getIvFluidOrder(){
        return ivFluidOrder;
    }

    public void setIvFluidOrder (IvFluidOrder order){
        ivFluidOrder = order;
    }

    @Override
    public Integer getId() {
        return orderStatusId;
    }

    @Override
    public void setId(Integer id) {
        orderStatusId = id;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
