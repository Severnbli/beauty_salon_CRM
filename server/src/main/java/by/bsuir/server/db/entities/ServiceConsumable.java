package by.bsuir.server.db.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Table(name = "services_consumables")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@IdClass(ServiceConsumableId.class)
public class ServiceConsumable {
    @Id
    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Id
    @ManyToOne
    @JoinColumn(name = "consumable_id", nullable = false)
    private Consumable consumable;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ServiceConsumable that = (ServiceConsumable) o;
        return getService() != null && Objects.equals(getService(), that.getService())
                && getConsumable() != null && Objects.equals(getConsumable(), that.getConsumable());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(service, consumable);
    }
}

