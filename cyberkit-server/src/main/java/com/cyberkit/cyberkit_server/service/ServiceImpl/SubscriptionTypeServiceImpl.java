package com.cyberkit.cyberkit_server.service.ServiceImpl;

import com.cyberkit.cyberkit_server.data.SubscriptionTypeEntity;
import com.cyberkit.cyberkit_server.dto.request.SubscriptionTypeDTO;
import com.cyberkit.cyberkit_server.exception.GeneralAllException;
import com.cyberkit.cyberkit_server.repository.SubscriptionTypeRepository;
import com.cyberkit.cyberkit_server.service.SubscriptionTypeService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class SubscriptionTypeServiceImpl implements SubscriptionTypeService {

    private final SubscriptionTypeRepository subscriptionTypeRepository;
    private final ModelMapper modelMapper;

    public SubscriptionTypeServiceImpl(SubscriptionTypeRepository subscriptionTypeRepository, ModelMapper modelMapper) {
        this.subscriptionTypeRepository = subscriptionTypeRepository;
        this.modelMapper = modelMapper;
    }
    @Override
    public List<SubscriptionTypeDTO> findAll() {
        List<SubscriptionTypeEntity> subscriptionTypeEntities  = subscriptionTypeRepository.findAll();
        List<SubscriptionTypeDTO> subscriptionTypeDTOS = new ArrayList<>(subscriptionTypeEntities.stream()
                .map(s -> modelMapper.map(s, SubscriptionTypeDTO.class)).toList());
        subscriptionTypeDTOS.sort(Comparator.comparing(SubscriptionTypeDTO::getId));
        return  subscriptionTypeDTOS;
    }

    @Override
    public void updateSubscriptionType(SubscriptionTypeDTO subscriptionTypeDTO) {
        if(subscriptionTypeDTO.getId() ==null)
            throw  new GeneralAllException("Invalid subscription id!!");
        if(!checkValidData(subscriptionTypeDTO))
            throw  new GeneralAllException("Bad credential!!");
        SubscriptionTypeEntity subscriptionTypeEntity = subscriptionTypeRepository.findById(subscriptionTypeDTO.getId()).get();
        subscriptionTypeEntity = modelMapper.map(subscriptionTypeDTO,SubscriptionTypeEntity.class);
        subscriptionTypeRepository.save(subscriptionTypeEntity);
    }

    @Override
    public Long getPriceFromSubscriptionType(Long subscriptionTypeId) {
        SubscriptionTypeEntity subscriptionTypeEntity = subscriptionTypeRepository.findById(subscriptionTypeId).get();
        return subscriptionTypeEntity.getPrice();
    }

    private  boolean checkValidData(SubscriptionTypeDTO subscriptionTypeDTO){
        List<SubscriptionTypeDTO> subscriptionTypeDTOS = new ArrayList<>();
        subscriptionTypeDTOS.add(subscriptionTypeDTO);
        for(int i =1 ;i<=3;i++){
            if(subscriptionTypeDTO.getId()==i) continue;
            SubscriptionTypeDTO typeDTO = modelMapper.map(subscriptionTypeRepository.findById((long) i).get(),SubscriptionTypeDTO.class);
            subscriptionTypeDTOS.add(typeDTO);
        }
        subscriptionTypeDTOS.sort(Comparator.comparing(SubscriptionTypeDTO::getId));
        for(int i=0 ; i<subscriptionTypeDTOS.size()-1;i++){
            if(subscriptionTypeDTOS.get(i).getDuration()>= subscriptionTypeDTOS.get(i+1).getDuration())
                return false;
            if(subscriptionTypeDTOS.get(i).getPrice()>= subscriptionTypeDTOS.get(i+1).getPrice())
                return false;
        }
        return true;
    }

}
