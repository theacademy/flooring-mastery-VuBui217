package com.sg.flooringmastery;

import com.sg.flooringmastery.controller.FlooringMasteryController;
import com.sg.flooringmastery.dao.*;
import com.sg.flooringmastery.service.FlooringMasteryServiceLayer;
import com.sg.flooringmastery.service.FlooringMasteryServiceLayerImpl;
import com.sg.flooringmastery.ui.FlooringMasteryView;
import com.sg.flooringmastery.ui.UserIO;
import com.sg.flooringmastery.ui.UserIOConsoleImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
    public static void main(String[] args) {
//        UserIO myIo = new UserIOConsoleImpl();
//        FlooringMasteryView myView = new FlooringMasteryView(myIo);
//        OrderDao myOrderDao = new OrderDaoFileImpl();
//        ProductDao myProductDao = new ProductDaoFileImpl();
//        TaxDao myTaxDao = new TaxDaoFileImpl();
//        FlooringMasteryServiceLayer myService = new FlooringMasteryServiceLayerImpl(myOrderDao, myProductDao, myTaxDao);
//        FlooringMasteryController controller = new FlooringMasteryController(myView, myService);
//        controller.run();

        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        FlooringMasteryController controller = ctx.getBean("controller", FlooringMasteryController.class);
        controller.run();
    }
}
