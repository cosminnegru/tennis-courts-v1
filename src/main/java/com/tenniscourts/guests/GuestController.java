package com.tenniscourts.guests;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/guests")
public class GuestController {

    private final GuestService guestService;

    @GetMapping("/{id}")
    @ApiOperation(value = "Find guest by id")
    public ResponseEntity<GuestDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(guestService.findById(id));
    }

    @GetMapping("/guest")
    @ApiOperation(value = "Find guest by name")
    public ResponseEntity<GuestDTO> findByName(@RequestParam(value = "name") String name) {
        return ResponseEntity.ok(guestService.findByName(name));
    }

    @GetMapping
    @ApiOperation(value = "Find all guests")
    public ResponseEntity<List<GuestDTO>> findAll() {
        return ResponseEntity.ok(guestService.findAll());
    }

    @PostMapping
    @ApiOperation(value = "Create a new guest")
    public ResponseEntity<GuestDTO> add(@RequestBody @Valid GuestDTO guestDTO) {
        return ResponseEntity.ok(guestService.add(guestDTO));
    }

    @PutMapping
    @ApiOperation(value = "Update a guest")
    public ResponseEntity<GuestDTO> update(@RequestBody @Valid GuestDTO guestDTO) {
        return ResponseEntity.ok(guestService.update(guestDTO));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete a guest")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        guestService.delete(id);
        return ResponseEntity.ok().build();
    }
}
