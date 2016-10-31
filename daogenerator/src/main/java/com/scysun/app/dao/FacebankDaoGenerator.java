package com.scysun.app.dao;

import java.io.File;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

/**
 * @author Phoenix
 */
public class FacebankDaoGenerator {
    private final static int SchemaVersion = 1001;

    private final static String DEFAULT_DAO_PACKAGE = "com.scysun.app.persistence";
    private final static String DEFAULT_ENTITY_PACKAGE = DEFAULT_DAO_PACKAGE + ".entity";

    public static void main(String[] args) throws Exception {
        File f = new File("dummy.txt");
        String rootFolder = f.getAbsoluteFile().getParent();
        System.out.println("Current working path is " + rootFolder);

        Schema schema = new Schema(SchemaVersion, "com.scysun.app");
        schema.setDefaultJavaPackageDao(DEFAULT_DAO_PACKAGE);

        addContact(schema);
//        addContactPhone(schema);

        new DaoGenerator().generateAll(schema, rootFolder + "/app/src-gen");
    }

    private static void addContact(Schema schema) {
        Entity contact = schema.addEntity("Contact");
        contact.setJavaPackage(DEFAULT_ENTITY_PACKAGE);
        contact.addIdProperty();
        contact.addStringProperty("firstName");
        contact.addStringProperty("lastName");
        contact.addDateProperty("birthday");

        //Id of Raw_Contact Table
        contact.addStringProperty("objectId");

        //Id of Contact table
        contact.addStringProperty("contactId");

        //If the contact is system user too
        contact.addStringProperty("userId");
        //TODO should use one-many table to store phone
        contact.addStringProperty("phone");
        //TODO should use one-many table to store phone
        contact.addStringProperty("email");

        contact.addStringProperty("organization");
        contact.addStringProperty("jobTitle");
        contact.addStringProperty("address");
    }

    private static void addContactPhone(Schema schema) {
        Entity customer = schema.addEntity("Customer");
        customer.addIdProperty();
        customer.addStringProperty("name").notNull();

        Entity order = schema.addEntity("Order");
        order.setTableName("ORDERS"); // "ORDER" is a reserved keyword
        order.addIdProperty();
        Property orderDate = order.addDateProperty("date").getProperty();
        Property customerId = order.addLongProperty("customerId").notNull().getProperty();
        order.addToOne(customer, customerId);

        ToMany customerToOrders = customer.addToMany(order, customerId);
        customerToOrders.setName("orders");
        customerToOrders.orderAsc(orderDate);
    }
}
