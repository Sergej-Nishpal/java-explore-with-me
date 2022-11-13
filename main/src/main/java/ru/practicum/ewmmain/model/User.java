package ru.practicum.ewmmain.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "users", schema = "public")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Override
    public boolean equals(Object user) {
        if (this == user) return true;
        if (!(user instanceof User)) return false;
        return id != null && id.equals(((User) user).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}