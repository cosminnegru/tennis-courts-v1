package com.tenniscourts.guests;

import com.tenniscourts.exceptions.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;
    private final GuestMapper guestMapper;

    @Transactional(readOnly = true)
    public GuestDTO findById(Long id) {
        return guestRepository.findById(id).map(guestMapper::map).orElseThrow(() -> {
            throw new EntityNotFoundException("Guest with id " + id + " not found.");
        });
    }

    @Transactional(readOnly = true)
    public GuestDTO findByName(String name) {
        return guestRepository.findByName(name).map(guestMapper::map).orElseThrow(() -> {
            throw new EntityNotFoundException("Guest with name " + name + " not found.");
        });
    }

    @Transactional(readOnly = true)
    public List<GuestDTO> findAll() {
        return guestRepository.findAll()
                .stream()
                .map(guestMapper::map)
                .collect(Collectors.toList());
    }

    @Transactional
    public GuestDTO add(GuestDTO guestDTO) {
        return guestMapper.map(guestRepository.save(guestMapper.map(guestDTO)));
    }

    @Transactional
    public GuestDTO update(GuestDTO guestDTO) {
        findById(guestDTO.getId());
        return guestMapper.map(guestRepository.save(guestMapper.map(guestDTO)));
    }

    @Transactional
    public void delete(Long guestId) {
        findById(guestId);
        guestRepository.deleteById(guestId);
    }
}
