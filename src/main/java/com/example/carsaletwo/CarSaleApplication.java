package com.example.carsaletwo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CarSaleApplication {

    private final CarService carService;

    @Autowired
    public CarSaleApplication(CarService carService) {
        this.carService = carService;
    }

    public static void main(String[] args) {
        SpringApplication.run(CarSaleApplication.class, args);
    }

    /**
     * This method runs after the application context is initialized.
     * It seeds initial data into the database via the CarService.
     */
    @PostConstruct
    public void init() {
        carService.seedData();
    }
}
