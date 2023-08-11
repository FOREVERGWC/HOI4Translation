package com.example.hoi4translation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.hoi4translation.domain.entity.Event;
import com.example.hoi4translation.mapper.EventMapper;
import com.example.hoi4translation.service.EventService;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl extends ServiceImpl<EventMapper, Event> implements EventService {

}




