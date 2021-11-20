package com.khizana.khizana.controllers;

import com.khizana.khizana.dao.models.Tool;
import com.khizana.khizana.dao.repositories.ToolRepository;
import org.dom4j.rule.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ToolViewsController {

    @Autowired
    private ToolRepository toolRepository;

    @Autowired
    private ToolController toolController;

    @GetMapping(value = "pages/tools")
    public ModelAndView tools() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("tools");
        return modelAndView;
    }

    @GetMapping(value = "pages/operations")
    public ModelAndView operations() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("operations");
        return modelAndView;
    }

    @GetMapping(value = "pages/nouvelle-entree")
    public ModelAndView nouvelleEntree() {
        if (((List<Tool>) toolRepository.findAll()).isEmpty()) {
            toolController.parseExcel();
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("nouvelle-entree");

        return modelAndView;
    }

    @GetMapping(value = "pages/nouvelle-sortie")
    public ModelAndView sortie() {
        if (((List<Tool>) toolRepository.findAll()).isEmpty()) {
            toolController.parseExcel();
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("nouvelle-sortie");

        return modelAndView;
    }


    @GetMapping(value = "entree")
    public ModelAndView entree() {
        if (((List<Tool>) toolRepository.findAll()).isEmpty()) {
            toolController.parseExcel();
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("entree");

        return modelAndView;
    }
}
