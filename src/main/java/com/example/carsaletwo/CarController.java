package com.example.carsaletwo;

import com.example.carsaletwo.Car;
import com.example.carsaletwo.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {
    @Autowired
    private CarRepository carRepository;

    @GetMapping
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    @PostMapping
    public Car createCar(@RequestBody Car car) {
        return carRepository.save(car);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable String id) {
        return carRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable String id, @RequestBody Car carDetails) {
        return carRepository.findById(id)
                .map(car -> {
                    car.setMake(carDetails.getMake());
                    car.setModel(carDetails.getModel());
                    car.setYear(carDetails.getYear());
                    car.setPrice(carDetails.getPrice());
                    return ResponseEntity.ok(carRepository.save(car));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable String id) {
        return carRepository.findById(id)
                .map(car -> {
                    carRepository.delete(car);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
