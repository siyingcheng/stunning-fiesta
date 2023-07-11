package com.simon.stunningfiesta.hogwartsuser;

import com.simon.stunningfiesta.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository hogwartsUserRepository;

    public UserService(UserRepository hogwartsUserRepository) {
        this.hogwartsUserRepository = hogwartsUserRepository;
    }

    public void deleteById(Integer id) {
        findById(id);
        hogwartsUserRepository.deleteById(id);
    }

    List<HogwartsUser> findAll() {
        return hogwartsUserRepository.findAll();
    }

    HogwartsUser findById(Integer id) {
        return hogwartsUserRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("user", id));
    }

    HogwartsUser save(HogwartsUser hogwartsUser) {
        return hogwartsUserRepository.save(hogwartsUser);
    }

    HogwartsUser update(Integer id, HogwartsUser hogwartsUser) {
        HogwartsUser userWillBeUpdate = findById(id);
        userWillBeUpdate.setUsername(hogwartsUser.getUsername())
                .setEnabled(hogwartsUser.isEnabled())
                .setRoles(hogwartsUser.getRoles());
        return hogwartsUserRepository.save(userWillBeUpdate);
    }
}
