DECLARE

  t_number jdbc_test.number_tbl := jdbc_test.number_tbl(1,2,3,4,5);
  v_result varchar;

 BEGIN

  v_result := jdbc_test.parent_child_pkg.table_to_string(t_number);
  dbms_output.put_line('v_result: ' || v_result);

 END;