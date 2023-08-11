package com.example.hoi4translation.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "`common/characters`")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Character extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId
    private String original;
    private String translation;
}
