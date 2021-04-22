package io.hust.pony.demoweb.meta;

import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "user")
public class UserMetaSpec implements MetaData<Integer> {
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @CreationTimestamp
    @Column(
            name = "create_time",
            updatable = false
    )
    private Date createTime;
    @UpdateTimestamp
    @Column(
            name = "last_update_time"
    )
    private Date lastUpdateTime;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private Integer age;

    @Column(name = "perseverance")
    private int perseverance;
}
