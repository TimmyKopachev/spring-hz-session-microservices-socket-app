package org.hz.session.integration.demo.service;

import org.hz.session.integration.demo.jpa.AbstractCrudService;
import org.hz.session.integration.demo.jpa.BaseRepository;
import org.hz.session.integration.demo.model.Player;
import org.hz.session.integration.demo.model.auth.AuthPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerService extends AbstractCrudService<Player> implements UserDetailsService {

    @Autowired
    private PlayerRepository playerRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var player = playerRepository.findPlayerByUsername(username);
        return new AuthPlayer(player);
    }

    public Player getCurrentAuthenticatedPlayerUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthPlayer userDetails = (AuthPlayer) authentication.getPrincipal();
        return Optional.of(userDetails)
                .map(AuthPlayer::getPlayer)
                .orElseThrow();
    }

    @Override
    protected BaseRepository<Player> getRepository() {
        return this.playerRepository;
    }
}
