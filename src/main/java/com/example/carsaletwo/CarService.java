package com.example.carsaletwo;

import com.example.carsaletwo.Car;
import com.example.carsaletwo.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class CarService {
    @Autowired
    private CarRepository carRepository;

    public void seedData() {
        if (carRepository.count() == 0) {
            carRepository.saveAll(Arrays.asList(
                new Car("Toyota", "Camry", 2020, 25000.0),
                new Car("Honda", "Accord", 2019, 22000.0),
                new Car("Ford", "Mustang", 2021, 35000.0)
            ));
        }
    }
}
