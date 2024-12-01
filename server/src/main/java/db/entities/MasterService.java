package db.entities;

import db.compoundIdentifiers.MasterServiceId;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Table(name = "masters_services")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@IdClass(MasterServiceId.class)
public class MasterService {
    @Id
    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "master_id", nullable = false)
    private Master master;

    @Id
    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        MasterService that = (MasterService) o;
        return getMaster() != null && Objects.equals(getMaster(), that.getMaster())
                && getService() != null && Objects.equals(getService(), that.getService());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(master, service);
    }
}

