
CREATE OR REPLACE PROCEDURE NewOrderItem(p_order ORDER_ITEM.ORDER_ID%type,
                                         p_menu_item ORDER_ITEM.MENU_ITEM_ID%type,
                                         p_count NUMBER,
                                         p_created_by ORDER_ITEM.CREATED_BY%type) AS
    v_remaining_count NUMBER;
    v_ordered_items_count NUMBER;
    v_adding_to_item ORDER_ITEM.ORDER_ITEM_ID%type;
    v_food_count_is_small_exc EXCEPTION;
    v_internal_exc EXCEPTION;

BEGIN
    SELECT COUNT INTO v_remaining_count FROM MENU_ITEM WHERE MENU_ITEM_ID = p_menu_item;
    v_remaining_count := v_remaining_count - p_count;
    PPRINT('Remains ' || TO_CHAR(v_remaining_count));

    IF v_remaining_count < 0 THEN
        raise v_food_count_is_small_exc;
    END IF;

    UPDATE MENU_ITEM
    SET COUNT = COUNT - p_count
    WHERE MENU_ITEM_ID = p_menu_item;

    SELECT COUNT(*) INTO v_ordered_items_count
    FROM ORDER_ITEM
    WHERE ORDER_ID = p_order AND
          MENU_ITEM_ID = p_menu_item AND
          STATE = 'Ordered';

    IF v_ordered_items_count = 0 THEN
        PPRINT('No item to Add Count');

        INSERT INTO ORDER_ITEM (ORDER_ITEM_ID, ORDER_ID, MENU_ITEM_ID, COUNT, CREATED_BY)
        VALUES ((SELECT COALESCE(MAX(ORDER_ITEM_ID), 0) + 1 FROM ORDER_ITEM), p_order, p_menu_item, p_count, p_created_by);

        PPRINT('Item Created');
    ELSE
        PPRINT('Item exist to Add Count');

        SELECT ORDER_ITEM_ID INTO v_adding_to_item FROM ORDER_ITEM
        WHERE ORDER_ID = p_order AND
              MENU_ITEM_ID = p_menu_item AND
              STATE = 'Ordered' AND
              rownum = 1;

        UPDATE ORDER_ITEM
        SET COUNT = COUNT + p_count
        WHERE ORDER_ITEM_ID = v_adding_to_item;

        PPRINT('Item ' || TO_CHAR(v_adding_to_item) || ' Updated');
    END IF;

    COMMIT;
EXCEPTION
    WHEN v_food_count_is_small_exc THEN
        PPRINT('Not Enough Food');
        ROLLBACK;
        raise v_food_count_is_small_exc;
    WHEN others THEN
        PPRINT('Internal Error');
        ROLLBACK;
        raise v_internal_exc;
END;


SELECT * FROM "ORDER";
SELECT * FROM ORDER_ITEM;
SELECT * FROM MENU;
SELECT * FROM MENU_ITEM;

CALL NewOrderItem(1, 1, 6, 1);