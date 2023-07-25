package com.league_buddies.backend.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.league_buddies.backend.configuration.SimpleGrantedAuthorityDeserializer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="user_table")
public class User implements UserDetails {
    @Id
    @GeneratedValue
    private long id;

    private String displayName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String emailAddress;

    private String leagueOfLegendsUserName;

    private Position favoritePosition;

    private String favoriteChampion;

    private String description;

    private PlayerType playerType;

    private float winRate;

    @Column(nullable = false)
    // TODO Learn what @Enumerated does.
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private Set<Server> servers;

    public User(String emailAddress, String password) {
        this.emailAddress = emailAddress;
        this.password = password;
    }

    public void setDisplayName(String displayName) {
        if (displayName != null && !displayName.isEmpty()) {
            this.displayName = displayName;
        }
    }

    public void setPassword(String password) {
        if (password != null && !password.isEmpty()) {
            this.password = password;
        }
    }

    public void setLeagueOfLegendsUserName(String leagueOfLegendsUserName) {
        if (leagueOfLegendsUserName != null && !leagueOfLegendsUserName.isEmpty()) {
            this.leagueOfLegendsUserName = leagueOfLegendsUserName;
        }
    }

    public void setFavoritePosition(Position favoritePosition) {
        if (favoritePosition != null) {
            this.favoritePosition = favoritePosition;
        }
    }

    public void setFavoriteChampion(String favoriteChampion) {
        if (favoriteChampion != null && !favoriteChampion.isEmpty()) {
            this.favoriteChampion = favoriteChampion;
        }
    }

    public void setDescription(String description) {
        if (description != null && !description.isEmpty()) {
            this.description = description;
        }
    }

    public void setPlayerType(PlayerType playerType) {
        if (playerType != null) {
            this.playerType = playerType;
        }
    }

    public void setWinRate(float winRate) {
        if (winRate > 0F) {
            this.winRate = winRate;
        }

    }

    public void setServers(Set<Server> servers) {
        if (servers != null && !servers.isEmpty()) {
            if (this.servers == null) {
                this.servers = servers;
            } else {
                for(Server server : servers) {
                    if (!this.servers.contains(server)) {
                        this.servers.add(server);
                    }
                }
            }
        }
    }

    public void setEmailAddress(String emailAddress) {
        if (emailAddress != null && !emailAddress.isEmpty()) {
            this.emailAddress = emailAddress;
        }
    }

    // TODO Learn about what this Annotation does.
    @JsonDeserialize(contentUsing = SimpleGrantedAuthorityDeserializer.class)
    @Override
    public Collection<? extends SimpleGrantedAuthority> getAuthorities() {
        return new ArrayList<>(List.of(new SimpleGrantedAuthority(role.name())));
    }

    @Override
    public String getUsername() {
        return this.emailAddress;
    }

    // We do not have a way to lock users or expire them etc so all of these return true for now.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
