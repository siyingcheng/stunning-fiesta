package com.simon.stunningfiesta.hogwartsuser;

import com.simon.stunningfiesta.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository hogwartsUserRepository;

    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository hogwartsUserRepository, PasswordEncoder passwordEncoder) {
        this.hogwartsUserRepository = hogwartsUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void deleteById(Integer id) {
        findById(id);
        hogwartsUserRepository.deleteById(id);
    }

    public List<HogwartsUser> findAll() {
        return hogwartsUserRepository.findAll();
    }

    public HogwartsUser findById(Integer id) {
        return hogwartsUserRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("user", id));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.hogwartsUserRepository.findByUsername(username)
                .map(MyUserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("username %s is not found.", username)));
    }

    public HogwartsUser save(HogwartsUser hogwartsUser) {
        hogwartsUser.setPassword(passwordEncoder.encode(hogwartsUser.getPassword()));
        return hogwartsUserRepository.save(hogwartsUser);
    }

    public HogwartsUser update(Integer id, HogwartsUser hogwartsUser) {
        HogwartsUser userWillBeUpdate = findById(id);
        userWillBeUpdate.setUsername(hogwartsUser.getUsername())
                .setEnabled(hogwartsUser.isEnabled())
                .setRoles(hogwartsUser.getRoles());
        return hogwartsUserRepository.save(userWillBeUpdate);
    }
}
