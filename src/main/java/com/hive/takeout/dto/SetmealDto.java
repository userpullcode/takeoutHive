package com.hive.takeout.dto;

import com.hive.takeout.entity.Setmeal;
import com.hive.takeout.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
