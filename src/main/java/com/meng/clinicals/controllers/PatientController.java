package com.meng.clinicals.controllers;

import com.meng.clinicals.model.ClinicalData;
import com.meng.clinicals.model.Patient;
import com.meng.clinicals.repos.PatientRepository;
import com.meng.clinicals.util.BMICalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class PatientController {

    private PatientRepository patientRepository;
    Map<String,String> filters=new HashMap<>();

    @Autowired
    PatientController(PatientRepository patientRepository){
        this.patientRepository = patientRepository;
    }

    @RequestMapping(value = "/patients", method = RequestMethod.GET)
    public List<Patient> getPatients(){
        return patientRepository.findAll();
    }

    @RequestMapping(value = "/patients/{id}", method = RequestMethod.GET)
    public Patient getPatient(@PathVariable("id") int id){
        Optional<Patient> obj = patientRepository.findById(id);
        if(obj.isPresent()){
            return obj.get();
        }
        return null;
    }

    @RequestMapping(value = "/patients", method = RequestMethod.POST)
    public Patient savePatient(@RequestBody Patient patient){
        return patientRepository.save(patient);
    }

    @RequestMapping(value = "/patients/analyze/{id}", method = RequestMethod.GET)
    public Patient analyze(@PathVariable("id") int id){
        Patient patient = patientRepository.findById(id).get();
        List<ClinicalData> clinicalData = patient.getClinicalData();
        ArrayList<ClinicalData> duplicateClinicalData = new ArrayList<>(clinicalData);
        for(ClinicalData entry : duplicateClinicalData){
            if(entry.getComponentName().equals("hw")){
                if(filters.containsKey(entry.getComponentName())){
                    clinicalData.remove(entry);
                    continue;
                }else{
                    filters.put(entry.getComponentName(), entry.getComponentValue());
                }

                BMICalculator.calculateBMI(clinicalData, entry);
            }
        }

        filters.clear();
        return patient;
    }



}
