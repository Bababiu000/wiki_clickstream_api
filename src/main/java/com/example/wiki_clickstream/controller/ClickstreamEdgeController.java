package com.example.wiki_clickstream.controller;


import com.example.wiki_clickstream.entity.ClickstreamEdge;
import com.example.wiki_clickstream.service.IClickstreamEdgeService;
import com.example.wiki_clickstream.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2023-10-21
 */
@RestController
@RequestMapping("/clickstream_edge")
public class ClickstreamEdgeController {

    @Autowired
    IClickstreamEdgeService clickstreamEdgeService;

    @GetMapping("/center/{date}")
    public ResponseEntity<Result<List<ClickstreamEdge>>> getCenterEdges(@PathVariable String date) {
        List<ClickstreamEdge> clickstreamEdges = clickstreamEdgeService.getCenterEdges(date);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS)) // 缓存1小时
                .body(Result.success(clickstreamEdges));
    }

    @GetMapping("/cluster/{date}")
    public ResponseEntity<Result<List<ClickstreamEdge>>> getClusterEdges(@PathVariable String date, @RequestParam Integer center) {
        List<ClickstreamEdge> clickstreamEdges = clickstreamEdgeService.getClusterEdges(date, center);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS)) // 缓存1小时
                .body(Result.success(clickstreamEdges));
    }

}