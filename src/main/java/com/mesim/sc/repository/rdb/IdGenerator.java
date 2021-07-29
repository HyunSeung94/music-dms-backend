package com.mesim.sc.repository.rdb;


import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;

public class IdGenerator extends SequenceStyleGenerator {

    public static final String PREFIX_KEY = "PREFIX";
    public static final String PREFIX_DEFAULT = "";
    private String prefix;

    public static final String FORMAT_KEY = "FORMAT";
    public static final String FORMAT_DEFAULT = "%d";
    private String format;

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        String id = preProcess(object);
        return id != null ? id : prefix + String.format(format, super.generate(session, object));
    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {

        super.configure(IntegerType.INSTANCE, params, serviceRegistry);

        prefix = ConfigurationHelper.getString(PREFIX_KEY, params, PREFIX_DEFAULT);
        format = ConfigurationHelper.getString(FORMAT_KEY, params, FORMAT_DEFAULT);
    }

    private String preProcess(Object object) {
        String id = null;


        return id;
    }
}
