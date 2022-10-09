package com.work.yygh.controller;

import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.work.yygh.common.result.Result;
import com.work.yygh.common.utils.MD5;
import com.work.yygh.model.hosp.HospitalSet;
import com.work.yygh.service.HospitalSetService;
import com.work.yygh.vo.hosp.HospitalSetQueryVo;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Random;

@Api(tags = "医院设置管理")
@RestController
@RequestMapping("admin/hosp/hospitalSet")
@CrossOrigin   //允许跨域
public class HospitalSetController {

    @Autowired
    HospitalSetService hospitalSetService;

    @GetMapping("/findAll")
    public Result findAll(){

        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    //逻辑删除
    @DeleteMapping("{id}")
    public Result removeHospSet(@PathVariable Long id){
        boolean flag=hospitalSetService.removeById(id);
        if(flag){

            return  Result.ok();
        }else {
            return Result.fail();
        }

    }

    //条件查询带分页
    @PostMapping("findPageHospSet/{current}/{limit}")
    public Result findPageHospSet(@PathVariable long current, @PathVariable long limit,@RequestBody HospitalSetQueryVo hospitalSetQueryVo){

        //创建page对象，传递当前页，每页记录数
        Page<HospitalSet> page = new Page<>(current, limit);

        //构建条件
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        String hoscode = hospitalSetQueryVo.getHascode();
        String hosname = hospitalSetQueryVo.getHasname();

        if (!StringUtils.isEmpty(hoscode)){
            wrapper.eq("hoscode",hospitalSetQueryVo.getHascode());
        }
        if (!StringUtils.isEmpty(hosname)){
            wrapper.like("hosname",hospitalSetQueryVo.getHasname());
        }




        //实现分页
        Page<HospitalSet> hospitalSetPage = hospitalSetService.page(page, wrapper);
        return Result.ok(hospitalSetPage);

    }

    //添加医院设置
    @PostMapping("/saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet){

        //设置状态1使用，0不能使用
        hospitalSet.setStatus(1);

        //签名密钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt()));

        //调用service
        boolean save = hospitalSetService.save(hospitalSet);

        if (save) {
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    //根据id查询
    @GetMapping("/getHospitalSet/{id}")
    public Result getHospitalSet(@PathVariable Long id){

        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return  Result.ok(hospitalSet);

    }

    //设置更新
    @PostMapping("/updataHospitalSet")
    public Result updataHospitalSet(@RequestBody HospitalSet hospitalSet){

        boolean flag = hospitalSetService.updateById(hospitalSet);

        if (flag ) {
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    //批量删除
    @DeleteMapping("/batchRemove")
    public Result batchRemoveHospitalSet(@RequestBody List<Long> idList){

        hospitalSetService.removeByIds(idList);
        return  Result.ok();

    }

    //医院设置锁定和解锁
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable Long id,@PathVariable int status){

        //根据id查询医院设置信息
        HospitalSet hospitalSet = hospitalSetService.getById(id);

        //设置状态
        hospitalSet.setStatus(status);

        //更新
        hospitalSetService.updateById(hospitalSet);

        return Result.ok();

    }

    //发送签名密钥
    @GetMapping("sendKey/{id}")
    public Result sendKeyHospitalSet(@PathVariable Long id){

        //根据id查询医院设置信息
        HospitalSet hospitalSet = hospitalSetService.getById(id);

        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();

        //TODO发送短信


        return Result.ok();

    }



}
