package org.example.carcatalog.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.carcatalog.cache.SimpleCache;
import org.example.carcatalog.model.Car;
import org.example.carcatalog.model.CarModel;
import org.example.carcatalog.model.exception.ModelNotFoundException;
import org.example.carcatalog.repository.CarModelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class CarModelService {

    private final CarModelRepository carModelRepository;
    private final SimpleCache<String,Object> modelSimpleCache;

    public List<CarModel> getAllModels(){
        return carModelRepository.findAll();
    }

    public CarModel getModel(Long id){
        CarModel carModel;
        if(modelSimpleCache.containsKey(id.toString())) {
            carModel = (CarModel) modelSimpleCache.get(id.toString());
            System.out.println("Read model from cache (get)");
        }
        else {
            carModel = carModelRepository.findById(id).orElseThrow(() -> new ModelNotFoundException(id));
            modelSimpleCache.put(id.toString(),carModel);
            System.out.println("Write model to cache (get)");
        }
        return carModel;
    }

    public List<CarModel> getCarModelsByCar(Long id){
        return carModelRepository.getCarModelsByCar(id);
    }

    public List<CarModel> getCarModelsByCarNative(Long id){
        return carModelRepository.getCarModelsByCarNative(id);
    }

    public CarModel saveModel(CarModel model){
        carModelRepository.save(model);
        if(!modelSimpleCache.containsKey(model.getId().toString())) {
            modelSimpleCache.put(model.getId().toString(), model);
            System.out.println("Write model to cache (post)");
        }
        return model;
    }
    @Transactional
    public CarModel updateModel(Long id, CarModel model){
        CarModel modelToUpdate = getModel(id);
        Car car = modelToUpdate.getCar();
        if(Objects.nonNull(car)){
            car.removeModel(modelToUpdate);
            car.addModel(model);
        }
        modelSimpleCache.remove(id.toString());
        modelToUpdate.setModel(model.getModel());
        modelSimpleCache.put(id.toString(),modelToUpdate);

        return modelToUpdate;
    }

    public void removeModel(Long id){
        CarModel model = getModel(id);
        Car car = model.getCar();
        if(Objects.nonNull(car)) {
            car.removeModel(model);
        }
        carModelRepository.delete(model);
        modelSimpleCache.remove(id.toString());
        System.out.println("Delete model from cache (delete)");
    }
}
