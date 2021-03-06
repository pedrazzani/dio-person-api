package br.com.pedrazzani.dio.personapi.service;

import br.com.pedrazzani.dio.personapi.dto.mapper.PersonMapper;
import br.com.pedrazzani.dio.personapi.dto.request.PersonDto;
import br.com.pedrazzani.dio.personapi.entity.Person;
import br.com.pedrazzani.dio.personapi.exception.PersonNotFoundException;
import br.com.pedrazzani.dio.personapi.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    public Optional<List<PersonDto>> findAll() {
        List<PersonDto> people = personRepository.findAll().stream()
                .map(personMapper::fromEntity)
                .toList();
        return people.isEmpty() ? Optional.empty() : Optional.of(people);
    }

    public Optional<PersonDto> createPerson(PersonDto personDto) {
        return Optional.ofNullable(personDto)
                .map(personMapper::toEntity)
                .map(personRepository::save)
                .map(personMapper::fromEntity);
    }

    public Optional<PersonDto> findById(Long id) {
        return personRepository.findById(id)
                .map(personMapper::fromEntity);
    }

    public void delete(Long id) {
        verifyIfExists(id);

        personRepository.deleteById(id);
        log.info("Person id {} deleted.", id);
    }

    public void update(Long id, PersonDto personDto) {
        verifyIfExists(id);

        Person personToUpdate = personMapper.toEntity(personDto);
        personToUpdate.setId(id);
        personRepository.save(personToUpdate);
        log.info("Person id {} updated.", id);
    }

    private Person verifyIfExists(Long id) {
        return personRepository.findById(id)
                .orElseThrow(PersonNotFoundException::new);
    }
}
