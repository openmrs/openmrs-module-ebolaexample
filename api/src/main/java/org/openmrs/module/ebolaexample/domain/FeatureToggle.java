package org.openmrs.module.ebolaexample.domain;

import org.openmrs.OpenmrsObject;
import org.openmrs.User;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "ebola_feature_toggle")
public class FeatureToggle  implements OpenmrsObject {

    @Id
    @GeneratedValue
    @Column(name = "feature_toggle_id")
    private Integer featureToggleId;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "enabled", nullable = false, columnDefinition = "boolean default false")
    private Boolean enabled = false;

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

    @Basic
    @Column(name = "uuid", length = 38, unique = true)
    private String uuid  = UUID.randomUUID().toString();

    public Integer getId() {
        return featureToggleId;
    }

    public void setId(Integer id) {
        featureToggleId = id;
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

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
