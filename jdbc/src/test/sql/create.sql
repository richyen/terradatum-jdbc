/* ON PPAS - MUST BE CONNECTED TO THE jdbc_test DATABASE AS SUPERUSER */

CREATE SEQUENCE JDBC_TEST.PARENT_ID_SEQ MINVALUE 1 MAXVALUE 9223372036854775807 INCREMENT BY 1 START WITH 1 CACHE 20;
CREATE SEQUENCE JDBC_TEST.CHILD_ID_SEQ MINVALUE 1 MAXVALUE 9223372036854775807 INCREMENT BY 1 START WITH 1 CACHE 20;

CREATE TABLE JDBC_TEST.PARENT
(
  PARENT_ID      NUMBER NOT NULL,
  PARENT_NAME    VARCHAR2(20),
  SOME_DATE      DATE,
  SOME_TIMESTAMP TIMESTAMP(6),
  IMAGE          BLOB,
  CONSTRAINT PARENT_PK PRIMARY KEY (PARENT_ID)
);

CREATE TABLE JDBC_TEST.SUB_PARENT
(
  PARENT_ID   NUMBER NOT NULL,
  PARENT_TYPE VARCHAR2(20),
  CONSTRAINT SUBPARENT_PK PRIMARY KEY (PARENT_ID),
  CONSTRAINT SUBPARENT_FK1 FOREIGN KEY (PARENT_ID)
  REFERENCES JDBC_TEST.PARENT (PARENT_ID) ON DELETE CASCADE
);

CREATE TABLE JDBC_TEST.CHILD
(
  CHILD_ID   NUMBER NOT NULL,
  PARENT_ID  NUMBER,
  CHILD_NAME VARCHAR2(20),
  CONSTRAINT CHILD_PK PRIMARY KEY (CHILD_ID),
  CONSTRAINT CHILD_FK1 FOREIGN KEY (PARENT_ID)
  REFERENCES JDBC_TEST.PARENT (PARENT_ID) ON DELETE CASCADE
);

CREATE OR REPLACE TRIGGER PARENT_ID_SEQ_TRG
BEFORE INSERT ON JDBC_TEST.PARENT
FOR EACH ROW
  BEGIN
    IF :NEW.PARENT_ID IS NULL
    THEN
      :NEW.PARENT_ID := JDBC_TEST.PARENT_ID_SEQ.NEXTVAL;
    END IF;
  END;
/

CREATE OR REPLACE TRIGGER CHILD_ID_SEQ_TRG
BEFORE INSERT ON JDBC_TEST.CHILD
FOR EACH ROW
  BEGIN
    IF :NEW.CHILD_ID IS NULL
    THEN
      :NEW.CHILD_ID := JDBC_TEST.CHILD_ID_SEQ.NEXTVAL;
    END IF;
  END;
/

CREATE OR REPLACE TYPE JDBC_TEST.CHILD_OBJ AS OBJECT
(
  CHILD_ID   NUMBER,
  CHILD_NAME VARCHAR(20)
);
/

CREATE OR REPLACE TYPE JDBC_TEST.CHILD_TBL AS TABLE OF JDBC_TEST.CHILD_OBJ;
/

CREATE OR REPLACE TYPE JDBC_TEST.SUB_PARENT_OBJ AS OBJECT
(
  SUB_PARENT_ID NUMBER,
  PARENT_TYPE   VARCHAR(20)
);
/

CREATE OR REPLACE TYPE JDBC_TEST.PARENT_OBJ AS OBJECT
(
  PARENT_ID      NUMBER,
  PARENT_NAME    VARCHAR(20),
  SUB_PARENT     JDBC_TEST.SUB_PARENT_OBJ,
  SOME_DATE      DATE,
  SOME_TIMESTAMP TIMESTAMP,
  IMAGE          BLOB,
  CHILDREN       JDBC_TEST.CHILD_TBL
);
/

CREATE OR REPLACE TYPE JDBC_TEST.PARENT_TBL AS TABLE OF JDBC_TEST.PARENT_OBJ;
/

CREATE OR REPLACE PACKAGE JDBC_TEST.PARENT_CHILD_PKG
IS

  FUNCTION get_parent_by_name(p_parent_name VARCHAR)
    RETURN JDBC_TEST.PARENT_OBJ;
  FUNCTION get_parents_by_children(p_children JDBC_TEST.CHILD_TBL)
    RETURN JDBC_TEST.PARENT_TBL;
  FUNCTION get_parents_by_type(p_parent_type VARCHAR)
    RETURN JDBC_TEST.PARENT_TBL;
  FUNCTION get_all_parent_objects(p_parents JDBC_TEST.PARENT_TBL)
    RETURN JDBC_TEST.PARENT_TBL;
  FUNCTION get_all_parent_objects(p_parent JDBC_TEST.PARENT_OBJ)
    RETURN JDBC_TEST.PARENT_OBJ;
  FUNCTION get_child_count_by_parent_obj(p_parent JDBC_TEST.PARENT_OBJ)
    RETURN NUMERIC;
  FUNCTION get_child_count_by_parent(p_parent JDBC_TEST.PARENT_OBJ)
    RETURN NUMERIC;
  FUNCTION get_child_count_by_parent(p_parent_id NUMERIC)
    RETURN NUMERIC;
  FUNCTION get_child_by_name(p_child_name VARCHAR)
    RETURN JDBC_TEST.CHILD_OBJ;
  FUNCTION get_children_by_parent(p_parent JDBC_TEST.PARENT_OBJ)
    RETURN JDBC_TEST.CHILD_TBL;
  FUNCTION get_children_by_parent_id(p_parent_id NUMERIC)
    RETURN JDBC_TEST.CHILD_TBL;

END PARENT_CHILD_PKG;
/

CREATE OR REPLACE PACKAGE BODY JDBC_TEST.PARENT_CHILD_PKG
IS
  FUNCTION get_parent_by_name(p_parent_name VARCHAR)
    RETURN JDBC_TEST.PARENT_OBJ IS

    r_parent JDBC_TEST.PARENT_OBJ;

    BEGIN

      SELECT JDBC_TEST.PARENT_OBJ(p.PARENT_ID, p.PARENT_NAME, NULL, p.SOME_DATE, p.SOME_TIMESTAMP, p.IMAGE, NULL)
      INTO r_parent
      FROM
        JDBC_TEST.PARENT p
      WHERE p.PARENT_NAME = p_parent_name;

      r_parent := get_all_parent_objects(r_parent);

      RETURN r_parent;

    END;

  FUNCTION get_parents_by_children(p_children JDBC_TEST.CHILD_TBL)
    RETURN JDBC_TEST.PARENT_TBL IS

    t_parent     JDBC_TEST.PARENT_TBL;
    r_parent     JDBC_TEST.PARENT_OBJ;
    r_sub_parent JDBC_TEST.SUB_PARENT_OBJ;
    t_child      JDBC_TEST.CHILD_TBL;
    l_index      NUMERIC;

    BEGIN

      SELECT JDBC_TEST.PARENT_OBJ(p.PARENT_ID, p.PARENT_NAME, NULL, p.SOME_DATE, p.SOME_TIMESTAMP, p.IMAGE, NULL)
      BULK COLLECT INTO t_parent
      FROM JDBC_TEST.PARENT p
      WHERE p.PARENT_ID IN (SELECT PARENT_ID
                            FROM TABLE (p_children));

      t_parent := get_all_parent_objects(t_parent);

      RETURN t_parent;

    END;

  FUNCTION get_parents_by_type(p_parent_type VARCHAR)
    RETURN JDBC_TEST.PARENT_TBL IS

    t_parent JDBC_TEST.PARENT_TBL;

    BEGIN

      SELECT JDBC_TEST.PARENT_OBJ(p.PARENT_ID, p.PARENT_NAME, NULL, p.SOME_DATE, p.SOME_TIMESTAMP, p.IMAGE, NULL)
      BULK COLLECT INTO t_parent
      FROM JDBC_TEST.PARENT p
        JOIN JDBC_TEST.SUB_PARENT sp ON p.PARENT_ID = sp.PARENT_ID
      WHERE sp.PARENT_TYPE = p_parent_type;

      t_parent := get_all_parent_objects(t_parent);

      RETURN t_parent;

    END;

  FUNCTION get_all_parent_objects(p_parents JDBC_TEST.PARENT_TBL)
    RETURN JDBC_TEST.PARENT_TBL IS

    t_parent JDBC_TEST.PARENT_TBL := p_parents;
    l_index  NUMERIC;

    BEGIN
      l_index := t_parent.FIRST;

      WHILE (l_index IS NOT NULL)
      LOOP
        t_parent(l_index) := get_all_parent_objects(t_parent(l_index));

        l_index := t_parent.NEXT(l_index);

      END LOOP;

      RETURN t_parent;
    END;

  FUNCTION get_all_parent_objects(p_parent JDBC_TEST.PARENT_OBJ)
    RETURN JDBC_TEST.PARENT_OBJ IS

    r_parent     JDBC_TEST.PARENT_OBJ := p_parent;
    r_sub_parent JDBC_TEST.SUB_PARENT_OBJ;
    t_child      JDBC_TEST.CHILD_TBL;

    BEGIN
      r_sub_parent := JDBC_TEST.SUB_PARENT_OBJ(NULL, NULL);
      t_child := JDBC_TEST.CHILD_TBL();

      SELECT JDBC_TEST.SUB_PARENT_OBJ(sp.PARENT_ID, sp.PARENT_TYPE)
      INTO r_sub_parent
      FROM JDBC_TEST.SUB_PARENT sp
      WHERE sp.PARENT_ID = r_parent.parent_id;

      r_parent.sub_parent := r_sub_parent;

      SELECT JDBC_TEST.CHILD_OBJ(c.CHILD_ID, c.CHILD_NAME)
      BULK COLLECT INTO t_child
      FROM JDBC_TEST.CHILD c
      WHERE c.PARENT_ID = r_parent.parent_id;

      r_parent.children := t_child;

      RETURN r_parent;

    END;

  FUNCTION get_child_count_by_parent_obj(p_parent JDBC_TEST.PARENT_OBJ)
    RETURN NUMERIC IS

    v_ret NUMERIC;

    BEGIN

      v_ret := get_child_count_by_parent(p_parent.parent_id);

      RETURN v_ret;

    END;

  FUNCTION get_child_count_by_parent(p_parent JDBC_TEST.PARENT_OBJ)
    RETURN NUMERIC IS

    v_ret NUMERIC;

    BEGIN

      v_ret := get_child_count_by_parent(p_parent.parent_id);

      RETURN v_ret;

    END;

  FUNCTION get_child_count_by_parent(p_parent_id NUMERIC)
    RETURN NUMERIC IS

    v_ret NUMERIC;

    BEGIN

      SELECT count(1)
      INTO v_ret
      FROM JDBC_TEST.CHILD c
      WHERE c.PARENT_ID = p_parent_id;

      RETURN v_ret;

    END;

  FUNCTION get_child_by_name(p_child_name VARCHAR)
    RETURN JDBC_TEST.CHILD_OBJ IS

    r_child JDBC_TEST.CHILD_OBJ;

    BEGIN

      SELECT JDBC_TEST.CHILD_OBJ(c.CHILD_ID, c.CHILD_NAME)
      INTO r_child
      FROM JDBC_TEST.CHILD c
      WHERE c.CHILD_NAME = p_child_name;

      RETURN r_child;

    END;

  FUNCTION get_children_by_parent(p_parent JDBC_TEST.PARENT_OBJ)
    RETURN JDBC_TEST.CHILD_TBL IS

    t_child JDBC_TEST.CHILD_TBL;

    BEGIN

      t_child := get_children_by_parent_id(p_parent.PARENT_ID);

      RETURN t_child;

    END;

  FUNCTION get_children_by_parent_id(p_parent_id NUMERIC)
    RETURN JDBC_TEST.CHILD_TBL IS

    t_child JDBC_TEST.CHILD_TBL;

    BEGIN

      SELECT JDBC_TEST.CHILD_OBJ(c.CHILD_ID, c.CHILD_NAME)
      BULK COLLECT INTO t_child
      FROM JDBC_TEST.CHILD c
      WHERE c.PARENT_ID = p_parent_id;

      RETURN t_child;

    END;


END PARENT_CHILD_PKG;
/

EXIT;