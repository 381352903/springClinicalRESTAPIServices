package com.meng.clinicals.controllers;

import com.meng.clinicals.dto.ClinicalDataRequest;
import com.meng.clinicals.model.ClinicalData;
import com.meng.clinicals.model.Patient;
import com.meng.clinicals.repos.ClinicalDataRepository;
import com.meng.clinicals.repos.PatientRepository;
import com.meng.clinicals.util.BMICalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ClinicalDataController {

    private ClinicalDataRepository clinicalDataRepository;
    private PatientRepository patientRepository;

    @Autowired
    ClinicalDataController(ClinicalDataRepository clinicalDataRepository, PatientRepository patientRepository){
        this.clinicalDataRepository = clinicalDataRepository;
        this.patientRepository = patientRepository;
    }

    @RequestMapping(value = "/clinicals", method = RequestMethod.POST)
    public ClinicalData saveClinicalData(@RequestBody ClinicalDataRequest request){

        Patient patient = patientRepository.findById(request.getPatientId()).get();
        ClinicalData clinicalData = new ClinicalData();
        clinicalData.setComponentName(request.getComponentName());
        clinicalData.setComponentValue(request.getComponentValue());
        clinicalData.setPatient(patient);


        return clinicalDataRepository.save(clinicalData);
    }

    @RequestMapping(value = "/clinicals/{patientId}/{componentName}", method = RequestMethod.GET)
    public List<ClinicalData> getClinicalData(@PathVariable("patientId") int patientId,
                                              @PathVariable("componentName") String componentName){
        if(componentName.equals("bmi")){
            componentName = "hw";
        }

        List<ClinicalData> clinicalData = clinicalDataRepository.findByPatientIdAndComponentNameOrderByMeasuredDateTime(patientId, componentName);

        ArrayList<ClinicalData> duplicateClinicalData = new ArrayList<>(clinicalData);
        for(ClinicalData entry : duplicateClinicalData){
            BMICalculator.calculateBMI(clinicalData, entry);
        }

        return clinicalData;
    }
}
