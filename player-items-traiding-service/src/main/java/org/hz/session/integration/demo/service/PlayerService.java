package org.hz.session.integration.demo.service;

public class PlayerService{

}
//
//@Service
//public class PlayerService extends AbstractCrudService<Player> implements UserDetailsService {
//
//    @Autowired
//    PlayerRepository playerRepository;
//
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        var player = playerRepository.findPlayerByUsername(username);
//        return new AuthPlayer(player);
//    }
//
//    public Player getCurrentAuthenticatedPlayerUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        AuthPlayer userDetails = (AuthPlayer) authentication.getPrincipal();
//        return Optional.of(userDetails)
//                .map(AuthPlayer::getPlayer)
//                .orElseThrow();
//    }
//
//    @Override
//    protected BaseRepository<Player> getRepository() {
//        return this.playerRepository;
//    }
//}
