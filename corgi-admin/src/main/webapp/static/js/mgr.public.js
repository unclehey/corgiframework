/**
 * 加载JqGrid数据
 * @param url 异步加载url
 * @param datatype 数据类型：local、json
 * @param grid_data JqGrid数据：如果datatype为json，则为空
 * @param colNames JqGrid列名
 * @param colModel JqGrid表体
 * @param footerrow JqGrid是否显示底部统计：true是，false否
 * @param caption JqGrid标题
 * @param shrinkToFit 是否压缩适应屏幕：true是，false否
 * @param multiselect 是否支持全选：true是，false否
 * @param tableId 列表id
 * @param pageId 分页id
 */
function reloadJqGrid(url, datatype, grid_data, colNames, colModel, footerrow, caption, shrinkToFit, multiselect, tableId, pageId) {
	jQuery(function($) {
		var grid_selector = "#grid-table";
		var pager_selector = "#grid-pager";
		if (typeof(tableId) != "undefined" && typeof(pageId) != "undefined") {
			grid_selector = "#" + tableId;
			pager_selector = "#" + pageId;
		}

		//resize to fit page size
		$(window).on('resize.jqGrid', function() {
			$(grid_selector).jqGrid('setGridWidth', $(".page-content").width());
		})
		//resize on sidebar collapse/expand
		var parent_column = $(grid_selector).closest('[class*="col-"]');
		$(document).on('settings.ace.jqGrid' , function(ev, event_name, collapsed) {
			if( event_name === 'sidebar_collapsed' || event_name === 'main_container_fixed' ) {
				//setTimeout is for webkit only to give time for DOM changes and then redraw!!!
				setTimeout(function() {
					$(grid_selector).jqGrid( 'setGridWidth', parent_column.width() );
				}, 0);
			}
		})

		jQuery(grid_selector).jqGrid({
			subGrid : false,
			url : url,
			datatype : datatype,
			mtype : "POST",
			data : grid_data,
			height : 530,
			colNames : colNames,
			colModel : colModel,

			viewrecords : true,
			rowNum : 20,
			rowList : [ 10, 20, 30, 50 ],
			pager : pager_selector,
			altRows : true,
			multiselect : multiselect,
			multiboxonly : true,
			rownumbers : true,
			rownumWidth : 50,
			shrinkToFit : shrinkToFit,
			autoScroll : true,
			footerrow: footerrow,

			loadComplete : function() {
				var table = this;
				setTimeout(function() {
					updatePagerIcons(table);
				}, 0);
			},
			gridComplete: function () {
				$(grid_selector + ' img').hover(function (e) {

					}, function () {

					}).click(function () {
						// 模态对话框显示
						$("#imgBig").attr('src', this.src);
						$('#imgBigModal').modal('show');
				});
			},
			caption : caption
		});
		$(window).triggerHandler('resize.jqGrid');//trigger window resize to make the grid get the correct size

		//navButtons
		jQuery(grid_selector).jqGrid('navGrid',pager_selector,
			{ 	//navbar options
				edit: false,
				add: false,
				del: false,
				search: true,
				searchicon : 'ace-icon fa fa-search orange',
				refresh: true,
				refreshicon : 'ace-icon fa fa-refresh green',
				view: false,
			},{},{},{},
			{
				//search form
				recreateForm: true,
				afterShowSearch: function(e){
					var form = $(e[0]);
					form.closest('.ui-jqdialog').find('.ui-jqdialog-title').wrap('<div class="widget-header" />')
					style_search_form(form);
				},
				afterRedraw: function(){
					style_search_filters($(this));
				}
				,
				multipleSearch: true,
			}
		)

		function style_search_filters(form) {
			form.find('.delete-rule').val('X');
			form.find('.add-rule').addClass('btn btn-xs btn-primary');
			form.find('.add-group').addClass('btn btn-xs btn-success');
			form.find('.delete-group').addClass('btn btn-xs btn-danger');
		}

		function style_search_form(form) {
			var dialog = form.closest('.ui-jqdialog');
			var buttons = dialog.find('.EditTable')
			buttons.find('.EditButton a[id*="_reset"]').addClass('btn btn-sm btn-info').find('.ui-icon').attr('class', 'ace-icon fa fa-retweet');
			buttons.find('.EditButton a[id*="_query"]').addClass('btn btn-sm btn-inverse').find('.ui-icon').attr('class', 'ace-icon fa fa-comment-o');
			buttons.find('.EditButton a[id*="_search"]').addClass('btn btn-sm btn-purple').find('.ui-icon').attr('class', 'ace-icon fa fa-search');
		}

		//replace icons with FontAwesome icons like above
		function updatePagerIcons(table) {
			var replacement = {
				'ui-icon-seek-first' : 'ace-icon fa fa-angle-double-left bigger-140',
				'ui-icon-seek-prev' : 'ace-icon fa fa-angle-left bigger-140',
				'ui-icon-seek-next' : 'ace-icon fa fa-angle-right bigger-140',
				'ui-icon-seek-end' : 'ace-icon fa fa-angle-double-right bigger-140'
			};
			$('.ui-pg-table:not(.navtable) > tbody > tr > .ui-pg-button > .ui-icon').each(function() {
				var icon = $(this);
				var $class = $.trim(icon.attr('class').replace('ui-icon', ''));

				if ($class in replacement) icon.attr('class', 'ui-icon '+replacement[$class]);
			})
		}

		$(document).one('ajaxloadstart.page', function(e) {
			$(grid_selector).jqGrid('GridUnload');
			$('.ui-jqdialog').remove();
		});
	});
}

//enable datepicker
function pickDate(elem) {
	jQuery(elem).datetimepicker({
		weekStart: 1,
		todayBtn:  1,
		autoclose: 1,
		todayHighlight: 1,
		startView: 2,
		forceParse: 0,
		minView: "month", //选择日期后，不会再跳转去选择时分秒
		language:  'zh-CN',
		format: 'yyyy-mm-dd'
	});
}