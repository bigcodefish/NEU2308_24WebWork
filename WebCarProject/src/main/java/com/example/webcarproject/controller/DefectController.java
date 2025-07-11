package com.example.webcarproject.controller;


import com.example.webcarproject.entity.Defect;
import com.example.webcarproject.entity.DefectStats;
import com.example.webcarproject.mapper.DefectMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/defects")
public class DefectController {

    private final DefectMapper defectMapper;


    public DefectController(DefectMapper defectMapper) {
        this.defectMapper = defectMapper;
    }

    @GetMapping
    public List<Defect> getByCondition(
            @RequestParam(required = false) String taskName,
            @RequestParam(required = false) String defectType,
            @RequestParam(required = false) Boolean isReal,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime,
            @RequestParam(required = false) Double minDistance,
            @RequestParam(required = false) Double maxDistance,
            @RequestParam(required = false) String discoverer,
            @RequestParam(required = false) String discoveryMethod,
            @RequestParam(required = false) String processor,
            @RequestParam(required = false) String location) { // 新增位置参数
        return defectMapper.getByCondition(taskName, defectType, isReal, status, severity,
                startTime, endTime, minDistance, maxDistance,
                discoverer, discoveryMethod, processor, location);
    }

    @PostMapping
    public String add(@RequestBody Defect defect) {
        defect.setStatus("待确认");
        defect.setReportTime(new Date());
        int result = defectMapper.insert(defect);
        return result > 0 ? "添加成功" : "添加失败";
    }

    // 添加备注接口
    @PostMapping("/{id}/remark")
    public String addRemark(@PathVariable Long id, @RequestBody String remark) {
        int result = defectMapper.updateRemark(id, remark);
        return result > 0 ? "备注添加成功" : "备注添加失败";
    }

    // 添加确认信息接口
    @PostMapping("/{id}/confirmation")
    public String addConfirmation(@PathVariable Long id,
                                  @RequestParam String confirmer) {
        int result = defectMapper.updateConfirmation(id, confirmer, new Date());
        return result > 0 ? "确认信息更新成功" : "更新失败";
    }

    // 添加处理开始时间接口
    @PutMapping("/{id}/start-processing")
    public String startProcessing(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String processor = request.get("processor");
        int result = defectMapper.updateProcessStartTime(id, new Date(), processor);
        return result > 0 ? "处理开始时间更新成功" : "更新失败";
    }

    // 添加处理完成时间接口
    @PutMapping("/{id}/complete-processing")
    public String completeProcessing(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String processResult = request.get("processResult");
        int result = defectMapper.updateProcessEndTime(id, new Date(), processResult);
        return result > 0 ? "处理完成时间更新成功" : "更新失败";
    }

    @PutMapping("/{id}/real")
    public String updateRealStatus(@PathVariable Long id, @RequestParam Boolean isReal) {
        int result = defectMapper.updateRealStatus(id, isReal);
        return result > 0 ? "更新成功" : "更新失败";
    }

    @PutMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam String status) {
        int result = defectMapper.updateStatus(id, status);
        return result > 0 ? "更新成功" : "更新失败";
    }

    @PutMapping("/batch/real")
    public String batchUpdateRealStatus(@RequestParam List<Long> ids, @RequestParam Boolean isReal) {
        int result = defectMapper.batchUpdateRealStatus(ids, isReal);
        return result > 0 ? "批量更新成功" : "批量更新失败";
    }

    @PutMapping("/batch/status")
    public String batchUpdateStatus(@RequestParam List<Long> ids, @RequestParam String status) {
        int result = defectMapper.batchUpdateStatus(ids, status);
        return result > 0 ? "批量更新成功" : "批量更新失败";
    }


    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        int result = defectMapper.deleteById(id);
        return result > 0 ? "删除成功" : "删除失败";
    }



    @GetMapping("/stats")
    public DefectStats getDefectStats() {
        return defectMapper.getDefectStats();
    }

    @GetMapping("/type-stats")
    public List<Map<String, Object>> getDefectTypeStats() {
        return defectMapper.getDefectTypeStats();
    }

    @GetMapping("/type-details")
    public Map<String, Object> getDefectTypeDetails(@RequestParam String defectType) {
        return defectMapper.getDefectTypeDetails(defectType);
    }

    // 添加月度统计接口
    @GetMapping("/monthly-stats")
    public List<Map<String, Object>> getMonthlyDefectStats() {
        return defectMapper.getMonthlyStats();
    }

    // 获取月度缺陷详情的接口
    @GetMapping("/monthly-details")
    public Map<String, Object> getMonthlyDefectDetails(
            @RequestParam String month,
            @RequestParam(defaultValue = "1000") int pageSize) {  // 获取更多数据

        Map<String, Object> result = new HashMap<>();

        // 获取该月缺陷列表
        List<Defect> defects = defectMapper.getMonthlyDefectDetails(month, pageSize);
        result.put("defects", defects);

        // 获取该月统计数据
        DefectStats stats = defectMapper.getMonthlyDefectStats(month);
        result.put("stats", stats);

        return result;
    }



}
