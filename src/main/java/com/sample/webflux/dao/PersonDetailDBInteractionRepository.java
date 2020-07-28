package com.sample.webflux.dao;

import com.sample.webflux.models.db.PersonDetailAccess;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

@Repository
@Slf4j
public class PersonDetailDBInteractionRepository implements DBInteraction<PersonDetailAccess,Integer>{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PersonDetailDBInteractionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Timed(
            value= "com.sample.webflux.daoPersonDetailDBInteractionRepository.getbyID",
            histogram =true,
            percentiles = {0.95,0.99},
            extraTags = {"version","1.0.0"}
    )
    public List<PersonDetailAccess> getbyID(Integer id) {
        String sql = "select * FROM system.user_message where ID = ?";
        return jdbcTemplate.query(sql,new Object[]{id}, mapPersonDetailsFomDb());
    }

    private RowMapper<PersonDetailAccess> mapPersonDetailsFomDb() {
        return (resultSet, i) -> {
            Integer id = resultSet.getInt("ID");
            String name = resultSet.getString("name");
            Integer age = resultSet.getInt("age");
            String dieseaseInfo = resultSet.getString("diseaseinfo");
            PersonDetailAccess personDetailAccess = new PersonDetailAccess();
            personDetailAccess.setAge(age);
            personDetailAccess.setDiseaseinfo(dieseaseInfo);
            personDetailAccess.setID(id);
            personDetailAccess.setName(name);
            return personDetailAccess;
        };
    }

    public List<PersonDetailAccess> getbyIDUsingStoredProcedure(Integer id) {

        List<SqlParameter> parameters = Arrays.asList(
                new SqlParameter(Types.INTEGER),
                new SqlOutParameter("ID",Types.INTEGER),
                new SqlOutParameter("diseaseinfo",Types.VARCHAR),
                new SqlOutParameter("message",Types.VARCHAR),
                new SqlOutParameter("name",Types.VARCHAR),
                new SqlOutParameter("age",Types.INTEGER));
        String sql = "{CALL SYSTEM.GETUSERDETAILS(?,?,?,?,?,?)}";
        Map<String, Object> abcd = null;
        try {
            abcd = jdbcTemplate.call(new CallableStatementCreator() {
                @Override
                public CallableStatement createCallableStatement(Connection con) throws SQLException {
                    CallableStatement callableStatement = con.prepareCall(sql);
                    callableStatement.setInt(1, id);
                    callableStatement.registerOutParameter(2, Types.INTEGER);
                    callableStatement.registerOutParameter(3, Types.VARCHAR);
                    callableStatement.registerOutParameter(4, Types.VARCHAR);
                    callableStatement.registerOutParameter(5, Types.VARCHAR);
                    callableStatement.registerOutParameter(6, Types.INTEGER);
                    return callableStatement;
                }
            }, parameters);
        }
        catch(Exception e){
            log.error("Exception calling procedure", e);
        }
        return getProcedureOutput(abcd);
         // return jdbcTemplate.query(sql,new Object[]{id}, mapPersonDetailsFomDb());
    }

    private  List<PersonDetailAccess> getProcedureOutput(Map<String, Object> abcd){
        if(abcd==null){
            return Collections.emptyList();
        }
        return new ArrayList<PersonDetailAccess>(){
            {
                add(createPersonDetaiiAccess(abcd));
            }
        };
    }
    private PersonDetailAccess createPersonDetaiiAccess(Map<String, Object> abcd) {
        PersonDetailAccess personDetailAccess = new PersonDetailAccess();
        personDetailAccess.setName(abcd.get("name").toString());
        personDetailAccess.setID((Integer)abcd.get("id"));
        personDetailAccess.setAge((Integer)abcd.get("age"));
        personDetailAccess.setDiseaseinfo(abcd.get("diseaseinfo").toString());
        return personDetailAccess;
    }

}
