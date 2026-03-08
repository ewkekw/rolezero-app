package com.role0.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.role0.adapter.out.persistence.entity.UsuarioJpaEntity;
import com.role0.adapter.out.persistence.mapper.PersistenceMapper;
// import com.role0.adapter.out.persistence.repository.SpringDataUsuarioRepository;
import com.role0.core.application.port.out.UsuarioRepositoryPort;
import com.role0.core.domain.usuario.entity.Usuario;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    // private final SpringDataUsuarioRepository repository;
    private final PersistenceMapper mapper;

    public UsuarioRepositoryAdapter(PersistenceMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void salvar(Usuario usuario) {
        UsuarioJpaEntity entity = mapper.toJpaEntity(usuario);
        // repository.save(entity); mock pro stubbing
    }

    @Override
    public Optional<Usuario> buscarPorId(UUID id) {
        // mock pro stubbing 
        // return repository.findById(id).map(mapper::toDomain);
        return Optional.empty();
    }
}
