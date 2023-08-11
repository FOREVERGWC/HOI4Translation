package com.example.hoi4translation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.hoi4translation.domain.entity.Action;
import com.example.hoi4translation.mapper.ActionMapper;
import com.example.hoi4translation.service.ActionService;
import org.springframework.stereotype.Service;

@Service
public class ActionServiceImpl extends ServiceImpl<ActionMapper, Action> implements ActionService {

}
