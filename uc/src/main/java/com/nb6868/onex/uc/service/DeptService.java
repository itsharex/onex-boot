package com.nb6868.onex.uc.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.jpa.DtoService;
import com.nb6868.onex.common.shiro.ShiroUser;
import com.nb6868.onex.common.shiro.ShiroUtils;
import com.nb6868.onex.common.util.ConvertUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.uc.UcConst;
import com.nb6868.onex.uc.dao.DeptDao;
import com.nb6868.onex.uc.dto.DeptDTO;
import com.nb6868.onex.uc.dto.DeptSaveOrUpdateReq;
import com.nb6868.onex.uc.entity.DeptEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 部门管理
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class DeptService extends DtoService<DeptDao, DeptEntity, DeptDTO> {

	@Autowired
	UserService userService;

	/**
	 * 新增或修改
	 */
	@Transactional(rollbackFor = Exception.class)
	public DeptEntity saveOrUpdateByReq(DeptSaveOrUpdateReq req) {
		// 检查请求
		// 转换数据格式
		DeptEntity entity;
		if (req.hasId()) {
			// 编辑数据
			entity = getById(req.getId());
			AssertUtils.isNull(entity, ErrorCode.DB_RECORD_NOT_EXISTED);
			BeanUtil.copyProperties(req, entity);
		} else {
			// 新增数据
			entity = BeanUtil.copyProperties(req, DeptEntity.class);
		}
		// 处理数据
		saveOrUpdateById(entity);
		return entity;
	}

	public DeptDTO getDtoByCode(String code) {
		//超级管理员，部门ID为null
		if (StrUtil.isBlank(code)) {
			return null;
		}
		return ConvertUtils.sourceToTarget(getOneByColumn("code", code), DeptDTO.class);
	}

	@Override
	public List<DeptDTO> listDto(Map<String, Object> params) {
		// 普通管理员，只能查询所属部门及子部门的数据
		ShiroUser user = ShiroUtils.getUser();
		if (user.getType() > UcConst.UserTypeEnum.DEPT_ADMIN.getCode()) {
			// params.put("deptIdList", getSubDeptIdList(user.getDeptId()));
		}

		// 查询部门列表
		List<DeptEntity> entityList = getBaseMapper().selectList(getWrapper("list", params));

		return ConvertUtils.sourceToTarget(entityList, DeptDTO.class);
	}

	/**
	 * 通过id获取父链
	 * @param code 组织代码
	 * @return 父链(包括自己)
	 */
	public List<DeptDTO> getParentChain(String code) {
		List<DeptDTO> chain = new ArrayList<>();
		if (StrUtil.isNotBlank(code)) {
			DeptDTO deptDTO = getDtoByCode(code);
			int loopCount = 0;
			while (deptDTO != null && loopCount < UcConst.DEPT_HIERARCHY_MAX) {
				chain.add(deptDTO);
				deptDTO = getDtoByCode(deptDTO.getPcode());
				loopCount++;
			}
			// 倒序
			Collections.reverse(chain);
		}
		return chain;
	}

	/**
	 * 根据部门ID，获取本部门及子部门ID列表
	 * @param id   部门ID
	 * @return 子部门列表
	 */
	/*public List<Long> getSubDeptIdList(Long id) {
		List<Long> deptIdList = getBaseMapper().getSubDeptIdList("%" + id + "%");
		deptIdList.add(id);

		return deptIdList;
	}*/

	/**
	 * 获取所有上级部门ID
	 *
	 * @param pid 上级ID
	 */
	/*private String getPidList(Long pid) {
		//顶级部门，无上级部门
		if (Const.DEPT_ROOT.equals(pid)) {
			return Const.DEPT_ROOT + "";
		}

		//所有部门的id、pid列表
		List<DeptEntity> deptList = getBaseMapper().getIdAndPidList();

		//list转map
		Map<Long, DeptEntity> map = new HashMap<>(deptList.size());
		for (DeptEntity entity : deptList) {
			map.put(entity.getId(), entity);
		}

		//递归查询所有上级部门ID列表
		List<Long> pidList = new ArrayList<>();
		getPidTree(pid, map, pidList);

		return CollUtil.join(pidList, ",");
	}

	private void getPidTree(String pcode, Map<Long, DeptEntity> map, List<Long> pidList) {
		// 顶级部门，无上级部门
		if (Const.DEPT_ROOT.equals(pid)) {
			return;
		}

		// 上级部门存在
		DeptEntity parent = map.get(pid);
		if (parent != null) {
			getPidTree(parent.getPid(), map, pidList);
		}

		pidList.add(pid);
	}*/
}
