package org.openmrs.module.ebolaexample.domain;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.DrugOrder;
import org.openmrs.User;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "ebola_scheduled_dose")
public class ScheduledDose extends BaseOpenmrsData {

    @Id
    @GeneratedValue
    @Column(name = "scheduled_dose_id")
    private Integer scheduledDoseId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private DrugOrder order;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "scheduled_datetime" )
    private Date scheduledDatetime;

    @Override
    public Integer getId() {
        return getScheduledDoseId();
    }

    @Override
    public void setId(Integer id) {
        setScheduledDoseId(id);
    }

    public Integer getScheduledDoseId() {
        return scheduledDoseId;
    }

    public void setScheduledDoseId(Integer scheduledDoseId) {
        this.scheduledDoseId = scheduledDoseId;
    }

    public DrugOrder getOrder() {
        return order;
    }

    public void setOrder(DrugOrder order) {
        this.order = order;
    }

    public Date getScheduledDatetime() {
        return scheduledDatetime;
    }

    public void setScheduledDatetime(Date scheduledDatetime) {
        this.scheduledDatetime = scheduledDatetime;
    }

    @ManyToOne
    @JoinColumn(name = "creator")
    @Override
    public User getCreator() {
        return super.getCreator();
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created" )
    @Override
    public Date getDateCreated() {
        return super.getDateCreated();
    }

    @ManyToOne
    @JoinColumn(name = "changed_by")
    @Override
    public User getChangedBy() {
        return super.getChangedBy();
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_changed" )
    @Override
    public Date getDateChanged() {
        return super.getDateChanged();
    }

    @Basic
    @Column(name = "uuid", length = 38, unique = true)
    @Override
    public String getUuid() {
        return super.getUuid();
    }
}
