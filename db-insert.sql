INSERT ALL
INTO ROLE (ROLE_ID, NAME, DESCRIPTION) VALUES (1, 'Manager', 'Main role of DB and App')
INTO ROLE (ROLE_ID, NAME, DESCRIPTION) VALUES (2, 'Service', 'People who serve food')
INTO ROLE (ROLE_ID, NAME, DESCRIPTION) VALUES (3, 'Chef', 'Creates food for customers')
SELECT 1 FROM DUAL;

INSERT ALL
INTO "USER" (USER_ID, FIRST_NAME, LAST_NAME, BORN_DATE, EMAIL, PASSWORD, ROLE_ID, ACTIVE) VALUES
    (1, 'Tomáš', 'Sobota', TO_DATE('2002-01-01', 'yyyy-mm-dd'), 'manager@gmail.com', '1234', 1, 1)
INTO "USER" (USER_ID, FIRST_NAME, LAST_NAME, BORN_DATE, EMAIL, PASSWORD, ROLE_ID, ACTIVE) VALUES
    (2, 'Ludvik', 'Trusty', TO_DATE('2001-09-11', 'yyyy-mm-dd'), 'chef@gmail.com', '1234', 3, 1)
INTO "USER" (USER_ID, FIRST_NAME, LAST_NAME, BORN_DATE, EMAIL, PASSWORD, ROLE_ID, ACTIVE) VALUES
    (3, 'Klára', 'Strong', TO_DATE('2000-01-01', 'yyyy-mm-dd'), 'service@gmail.com', '1234', 2, 1)
INTO "USER" (USER_ID, FIRST_NAME, LAST_NAME, BORN_DATE, EMAIL, PASSWORD, ROLE_ID, ACTIVE) VALUES
    (4, 'Laura', 'New', TO_DATE('2000-12-10', 'yyyy-mm-dd'), 'laura@gmail.com', '1234', 2, 1)
INTO "USER" (USER_ID, FIRST_NAME, LAST_NAME, BORN_DATE, EMAIL, PASSWORD, ROLE_ID, ACTIVE) VALUES
    (5, 'Emma', 'Nobody', TO_DATE('2001-01-01', 'yyyy-mm-dd'), 'emma64@gmail.com', '1234', 2, 0)
INTO "USER" (USER_ID, FIRST_NAME, LAST_NAME, BORN_DATE, EMAIL, PASSWORD, ROLE_ID, ACTIVE) VALUES
    (6, 'Mirek', 'Luboš', TO_DATE('2000-12-24', 'yyyy-mm-dd'), 'lubos@gmail.com', '1234', 3, 1)
INTO "USER" (USER_ID, FIRST_NAME, LAST_NAME, BORN_DATE, EMAIL, PASSWORD, ROLE_ID, ACTIVE) VALUES
    (7, 'Marek', 'Kuchař', TO_DATE('2000-01-01', 'yyyy-mm-dd'), 'marek@gmail.com', '1234', 3, 0)
SELECT 1 FROM DUAL;

INSERT ALL
INTO TYPE (TYPE_ID, NAME) VALUES (1, 'Food')
INTO TYPE (TYPE_ID, NAME) VALUES (2, 'Drink')
SELECT 1 FROM DUAL;

INSERT ALL
INTO FOOD (FOOD_ID, TYPE_ID, NAME, DESCRIPTION, ALLERGENS, COST) VALUES (1, 1, 'Pizza', 'Best Italian food', 'ABCD', 180)
INTO FOOD (FOOD_ID, TYPE_ID, NAME, DESCRIPTION, ALLERGENS, COST) VALUES (2, 1, 'Lasagna', 'Second Italian food', 'ABCD', 210)
INTO FOOD (FOOD_ID, TYPE_ID, NAME, DESCRIPTION, ALLERGENS, COST) VALUES (3, 1, 'Pasta', NULL, 'ABCD', 210)
INTO FOOD (FOOD_ID, TYPE_ID, NAME, DESCRIPTION, ALLERGENS, COST) VALUES (4, 2, 'CocaCola', NULL, 'A', 30)
INTO FOOD (FOOD_ID, TYPE_ID, NAME, DESCRIPTION, ALLERGENS, COST) VALUES (5, 1, 'Ravioli', 'The word ravioli denotes various kinds of pasta ma', 'ABCD', 120)
INTO FOOD (FOOD_ID, TYPE_ID, NAME, DESCRIPTION, ALLERGENS, COST) VALUES (6, 1, 'Burrito', 'Burrito is a dish consisting of a wheat flour tort', 'ABCD', 100)
INTO FOOD (FOOD_ID, TYPE_ID, NAME, DESCRIPTION, ALLERGENS, COST) VALUES (7, 1, 'Chicken', '', 'ABCD', 70)
INTO FOOD (FOOD_ID, TYPE_ID, NAME, DESCRIPTION, ALLERGENS, COST) VALUES (8, 1, 'Pasta', 'The carbonara we know today is prepared by simply ', 'ABCD', 110)
INTO FOOD (FOOD_ID, TYPE_ID, NAME, DESCRIPTION, ALLERGENS, COST) VALUES (9, 1, 'Gyoza', 'The famous Japanese gyoza are crescent-shaped dump', 'ABCD', 90)
INTO FOOD (FOOD_ID, TYPE_ID, NAME, DESCRIPTION, ALLERGENS, COST) VALUES (10, 1, 'Cupcake', 'A cupcake is a tiny cake that is baked in a thin p', 'ABCD', 200)
INTO FOOD (FOOD_ID, TYPE_ID, NAME, DESCRIPTION, ALLERGENS, COST) VALUES (11, 1, 'Risotto', 'This widely popular and extremely versatile group ', 'ABCD', 150)
INTO FOOD (FOOD_ID, TYPE_ID, NAME, DESCRIPTION, ALLERGENS, COST) VALUES (12, 1, 'Mochi', 'Mochi, the tiny cakes made out of glutinous rice. ', 'ABCD', 60)
INTO FOOD (FOOD_ID, TYPE_ID, NAME, DESCRIPTION, ALLERGENS, COST) VALUES (13, 2, 'Fanta', NULL, 'A', 25)
INTO FOOD (FOOD_ID, TYPE_ID, NAME, DESCRIPTION, ALLERGENS, COST) VALUES (14, 2, 'Green Tea', NULL, 'C', 10)
SELECT 1 FROM DUAL;

INSERT INTO ORDER_ITEM (ORDER_ITEM_ID, ORDER_ID, MENU_ITEM_ID, COUNT, CREATED_BY)
VALUES (2, 1, 2, 3, 1);

COMMIT;