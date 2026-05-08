package com.sg.flooringmastery;

import com.sg.flooringmastery.controller.FlooringMasteryController;
import com.sg.flooringmastery.dao.OrderDao;
import com.sg.flooringmastery.dao.OrderDaoFileImpl;
import com.sg.flooringmastery.dao.ProductDao;
import com.sg.flooringmastery.dao.ProductDaoFileImpl;
import com.sg.flooringmastery.model.Order;
import com.sg.flooringmastery.service.FlooringMasteryServiceLayer;
import com.sg.flooringmastery.service.FlooringMasteryServiceLayerImpl;
import com.sg.flooringmastery.ui.FlooringMasterView;
import com.sg.flooringmastery.ui.UserIO;
import com.sg.flooringmastery.ui.UserIOConsoleImpl;

public class app {
    public static void main(String[] args) {
        UserIO myIo = new UserIOConsoleImpl();
        FlooringMasterView myView = new FlooringMasterView(myIo);
        OrderDao myOrderDao = new OrderDaoFileImpl();

        FlooringMasteryServiceLayer myService = new FlooringMasteryServiceLayerImpl(myOrderDao);
        FlooringMasteryController controller = new FlooringMasteryController(myView, myService);
        controller.run();
    }
}
