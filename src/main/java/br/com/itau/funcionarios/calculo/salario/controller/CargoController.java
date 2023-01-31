package br.com.itau.funcionarios.calculo.salario.controller;

import br.com.itau.funcionarios.calculo.salario.dto.response.CargoResponseDTO;
import br.com.itau.funcionarios.calculo.salario.dto.request.CargoSaveRequestDTO;
import br.com.itau.funcionarios.calculo.salario.dto.response.CargoResponseListDTO;
import br.com.itau.funcionarios.calculo.salario.dto.response.CargoSaveResponseDTO;
import br.com.itau.funcionarios.calculo.salario.dto.response.FuncionarioListaResponseDTO;
import br.com.itau.funcionarios.calculo.salario.entity.Cargo;
import br.com.itau.funcionarios.calculo.salario.entity.Funcionario;
import br.com.itau.funcionarios.calculo.salario.service.CargoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@RestController
@RequestMapping ("/cargos")
public class CargoController {

    private CargoService cargoService;

    public CargoController(CargoService cargoService){
        this.cargoService = cargoService;

    }


    @PostMapping
    public ResponseEntity<CargoSaveResponseDTO> save (@RequestBody @Valid CargoSaveRequestDTO cargoSaveRequestDTO){

        log.info(cargoSaveRequestDTO.toString());

        Cargo cargo = new Cargo();

        cargo.setDescricaoCargo(cargoSaveRequestDTO.getDescricaoCargo());
        cargo.setNomeCargo(cargoSaveRequestDTO.getNomeCargo());
        cargo.setSalarioBase(cargoSaveRequestDTO.getSalarioBase());

        cargo = cargoService.save(cargo);

        CargoSaveResponseDTO cargoSaveResponseDTO = new CargoSaveResponseDTO();
        cargoSaveResponseDTO.setId(cargo.getIdCargo());

        return ResponseEntity.status(HttpStatus.CREATED).body(cargoSaveResponseDTO);
    }

    @DeleteMapping(value = "/{idCargo}")
    public ResponseEntity <String> delete (@PathVariable  Long idCargo) {

        Optional<Cargo> cargo = cargoService.findById(idCargo);
        if (cargo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

       cargoService.delete(idCargo);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{idCargo}")
    public ResponseEntity<CargoResponseDTO> getById (@PathVariable(value= "idCargo") Long id){

        Optional<Cargo> cargo = cargoService.findById(id);

        if (cargo.isPresent()) {
            CargoResponseDTO cargoResponseDTO = new CargoResponseDTO();

            cargoResponseDTO.setNomeCargo(cargo.get().getNomeCargo());
            cargoResponseDTO.setIdCargo(cargo.get().getIdCargo());
            cargoResponseDTO.setDescricaoCargo(cargo.get().getDescricaoCargo());
            cargoResponseDTO.setSalarioBase(cargo.get().getSalarioBase());

           cargoResponseDTO.setFuncionarios(new HashSet<>());
           for (Funcionario funcionario: cargo.get().getFuncionarios()){
               FuncionarioListaResponseDTO funcionarioListaResponseDTO = new FuncionarioListaResponseDTO();
               funcionarioListaResponseDTO.setNome(funcionario.getNome());
               funcionarioListaResponseDTO.setMatricula(funcionario.getMatricula());
               funcionarioListaResponseDTO.setSexo(funcionario.getSexo());
               funcionarioListaResponseDTO.setEndereco(funcionario.getEndereco());
               funcionarioListaResponseDTO.setDtNasc(funcionario.getDtNasc());
               funcionarioListaResponseDTO.setValorBonus(funcionario.getValorBonus());

               cargoResponseDTO.getFuncionarios().add(funcionarioListaResponseDTO);
           }



            return ResponseEntity.ok(cargoResponseDTO);

        }
        else {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @GetMapping
    public ResponseEntity <List<CargoResponseDTO>> findAll(){

        List<Cargo> cargos = cargoService.findAll();
        List<CargoResponseDTO> cargoResponseDTOS = new ArrayList<>();
        for (Cargo cargo: cargos) {

            CargoResponseDTO cargoResponseDTO = new CargoResponseDTO();

            cargoResponseDTO.setNomeCargo(cargo.getNomeCargo());
            cargoResponseDTO.setIdCargo(cargo.getIdCargo());
            cargoResponseDTO.setDescricaoCargo(cargo.getDescricaoCargo());
            cargoResponseDTO.setSalarioBase(cargo.getSalarioBase());

            cargoResponseDTO.setFuncionarios(new HashSet<>());
            for (Funcionario funcionario: cargo.getFuncionarios()){
                FuncionarioListaResponseDTO funcionarioListaResponseDTO = new FuncionarioListaResponseDTO();
                funcionarioListaResponseDTO.setNome(funcionario.getNome());
                funcionarioListaResponseDTO.setMatricula(funcionario.getMatricula());
                funcionarioListaResponseDTO.setSexo(funcionario.getSexo());
                funcionarioListaResponseDTO.setEndereco(funcionario.getEndereco());
                funcionarioListaResponseDTO.setDtNasc(funcionario.getDtNasc());
                funcionarioListaResponseDTO.setValorBonus(funcionario.getValorBonus());

                cargoResponseDTO.getFuncionarios().add(funcionarioListaResponseDTO);
            }
           cargoResponseDTOS.add(cargoResponseDTO);
        }


        return ResponseEntity.ok(cargoResponseDTOS);
    }

    @PutMapping("/{idCargo}")
    public ResponseEntity update(@PathVariable Long idCargo, @RequestBody @Valid CargoSaveRequestDTO cargoRequest) {

        Optional<Cargo> cargo = cargoService.findById(idCargo);

        if (cargo.isPresent()) {
            cargo.get().setSalarioBase(cargoRequest.getSalarioBase());
            cargo.get().setDescricaoCargo(cargoRequest.getDescricaoCargo());
            cargo.get().setNomeCargo(cargoRequest.getNomeCargo());

            cargoService.save(cargo.get());
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @PatchMapping("/{idCargo}/{salarioBase}")
    public ResponseEntity updateSalario(@PathVariable Long idCargo, @PathVariable BigDecimal salarioBase) {

        log.info("idCargo: {} salarioBase: {}", idCargo, salarioBase);

        Optional<Cargo> cargo = cargoService.findById(idCargo);

        if (cargo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            cargo.get().setSalarioBase(salarioBase);
            cargoService.save(cargo.get());
            return ResponseEntity.ok().build();
        }
    }

}
