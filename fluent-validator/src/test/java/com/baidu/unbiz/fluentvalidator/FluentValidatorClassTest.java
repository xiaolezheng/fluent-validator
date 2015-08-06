package com.baidu.unbiz.fluentvalidator;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.baidu.unbiz.fluentvalidator.dto.Car;
import com.baidu.unbiz.fluentvalidator.rpc.ManufacturerService;
import com.baidu.unbiz.fluentvalidator.rpc.impl.ManufacturerServiceImpl;
import com.baidu.unbiz.fluentvalidator.validator.CarValidator;

/**
 * @author zhangxu
 */
public class FluentValidatorClassTest {

    private ManufacturerService manufacturerService = new ManufacturerServiceImpl();

    @Test
    public void testCar() {
        Car car = getValidCar();

        Closure<List<String>> closure = new ClosureHandler<List<String>>() {

            private List<String> allManufacturers;

            @Override
            public List<String> getResult() {
                return allManufacturers;
            }

            @Override
            public void doExecute(Object... input) {
                allManufacturers = manufacturerService.getAllManufacturers();
            }
        };

        System.out.println(closure.getResult());
        assertThat(closure.getResult(), nullValue());

        ValidatorChain chain = new ValidatorChain();
        List<Validator> validators = new ArrayList<Validator>();
        validators.add(new CarValidator());
        chain.setValidators(validators);

        Result ret = FluentValidator.checkAll()
                .putClosure2Context("manufacturerClosure", closure)
                .on(car, chain)
                .doValidate();
        System.out.println(ret);
        assertThat(ret.hasNoError(), is(true));

        System.out.println(closure.getResult());
        assertThat(closure.getResult().size(), is(3));
    }

    private Car getValidCar() {
        return new Car("BMW", "LA1234", 5);
    }

}
