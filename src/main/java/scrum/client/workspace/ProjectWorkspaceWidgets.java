/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>, Artjom Kochtchi
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package scrum.client.workspace;

import ilarkesto.core.scope.Scope;
import ilarkesto.gwt.client.AGwtEntity;
import ilarkesto.gwt.client.AWidget;
import ilarkesto.gwt.client.EntityDoesNotExistException;
import ilarkesto.gwt.client.SwitcherWidget;
import scrum.client.ScrumGwt;
import scrum.client.admin.ProjectUserConfigWidget;
import scrum.client.admin.PunishmentsWidget;
import scrum.client.admin.SystemConfigWidget;
import scrum.client.admin.SystemMessageManagerWidget;
import scrum.client.admin.User;
import scrum.client.admin.UserListWidget;
import scrum.client.calendar.CalendarWidget;
import scrum.client.calendar.SimpleEvent;
import scrum.client.collaboration.Chat;
import scrum.client.collaboration.ForumSupport;
import scrum.client.collaboration.ForumWidget;
import scrum.client.collaboration.Subject;
import scrum.client.collaboration.WikiWidget;
import scrum.client.collaboration.Wikipage;
import scrum.client.context.UserHighlightSupport;
import scrum.client.core.RequestEntityByReferenceServiceCall;
import scrum.client.core.RequestEntityServiceCall;
import scrum.client.dashboard.DashboardWidget;
import scrum.client.files.File;
import scrum.client.files.FileRepositoryWidget;
import scrum.client.impediments.Impediment;
import scrum.client.impediments.ImpedimentListWidget;
import scrum.client.issues.Issue;
import scrum.client.issues.IssueManagementWidget;
import scrum.client.journal.JournalWidget;
import scrum.client.journal.ProjectEvent;
import scrum.client.pr.BlogEntry;
import scrum.client.pr.BlogWidget;
import scrum.client.project.ProductBacklogWidget;
import scrum.client.project.Project;
import scrum.client.project.ProjectAdminWidget;
import scrum.client.project.ProjectDataReceivedEvent;
import scrum.client.project.ProjectDataReceivedHandler;
import scrum.client.project.ProjectOverviewWidget;
import scrum.client.project.Quality;
import scrum.client.project.QualityBacklogWidget;
import scrum.client.project.Requirement;
import scrum.client.release.Release;
import scrum.client.release.ReleaseManagementWidget;
import scrum.client.risks.Risk;
import scrum.client.risks.RiskListWidget;
import scrum.client.search.Search;
import scrum.client.search.SearchResultsWidget;
import scrum.client.sprint.NextSprintWidget;
import scrum.client.sprint.Sprint;
import scrum.client.sprint.SprintBacklogWidget;
import scrum.client.sprint.SprintHistoryWidget;
import scrum.client.sprint.Task;
import scrum.client.tasks.WhiteboardWidget;

import com.google.gwt.user.client.ui.Widget;

public class ProjectWorkspaceWidgets extends GProjectWorkspaceWidgets implements ProjectDataReceivedHandler {

	private ProjectSidebarWidget sidebar = new ProjectSidebarWidget();
	private DashboardWidget dashboard;
	private ProjectOverviewWidget projectOverview;
	private WhiteboardWidget whiteboard;
	private SprintBacklogWidget sprintBacklog;
	private ProductBacklogWidget productBacklog;
	private QualityBacklogWidget qualityBacklog;
	private ForumWidget forum;
	private CalendarWidget calendar;
	private NextSprintWidget nextSprint;
	private ImpedimentListWidget impedimentList;
	private IssueManagementWidget issueList;
	private RiskListWidget riskList;
	private ReleaseManagementWidget releaseList;
	private WikiWidget wiki;
	private SprintHistoryWidget sprintHistory;
	private ProjectUserConfigWidget projectUserConfig;
	private PunishmentsWidget punishments;
	private JournalWidget projectEventList;
	private FileRepositoryWidget fileRepository;
	private BlogWidget blog;
	private ProjectAdminWidget projectAdmin;
	private SystemConfigWidget systemConfig;
	private SystemMessageManagerWidget systemMessageManager;
	private UserListWidget userList;

	private PageSet pages = new PageSet();

	private boolean searchResultsAdded;

	private User highlightedUser;

	@Override
	public void initialize() {
		projectOverview = new ProjectOverviewWidget();
		dashboard = new DashboardWidget();

		pages.addPage(new Page(dashboard, "Dashboard", null));

		String sprintGroupKey = "sprint";
		whiteboard = new WhiteboardWidget();
		pages.addPage(new Page(whiteboard, "Whiteboard", sprintGroupKey));
		sprintBacklog = new SprintBacklogWidget();
		pages.addPage(new Page(sprintBacklog, "Sprint Backlog", sprintGroupKey));

		String productGroupKey = "product";
		productBacklog = new ProductBacklogWidget();
		pages.addPage(new Page(productBacklog, "Product Backlog", productGroupKey));
		qualityBacklog = new QualityBacklogWidget();
		pages.addPage(new Page(qualityBacklog, "Qualities", productGroupKey));
		issueList = new IssueManagementWidget();
		pages.addPage(new Page(issueList, "Issues", productGroupKey));
		releaseList = new ReleaseManagementWidget();
		pages.addPage(new Page(releaseList, "Releases", productGroupKey));

		String projectGroupKey = "project";
		impedimentList = new ImpedimentListWidget();
		pages.addPage(new Page(impedimentList, "Impediments", projectGroupKey));
		riskList = new RiskListWidget();
		pages.addPage(new Page(riskList, "Risks", projectGroupKey));
		projectEventList = new JournalWidget();
		pages.addPage(new Page(projectEventList, "Journal", projectGroupKey));
		nextSprint = new NextSprintWidget();
		pages.addPage(new Page(nextSprint, "Next Sprint", projectGroupKey));
		sprintHistory = new SprintHistoryWidget();
		pages.addPage(new Page(sprintHistory, "Sprint History", projectGroupKey));

		String collaborationGroupKey = "collaboration";
		wiki = new WikiWidget();
		pages.addPage(new Page(wiki, "Wiki", collaborationGroupKey));
		forum = new ForumWidget();
		pages.addPage(new Page(forum, "Forum", collaborationGroupKey));
		calendar = new CalendarWidget();
		pages.addPage(new Page(calendar, "Calendar", collaborationGroupKey));
		fileRepository = new FileRepositoryWidget();
		pages.addPage(new Page(fileRepository, "File Repository", collaborationGroupKey));
		punishments = new PunishmentsWidget();
		pages.addPage(new Page(punishments, "Courtroom", collaborationGroupKey));

		String administrationKey = "administration";
		blog = new BlogWidget();
		pages.addPage(new Page(blog, "Blog", administrationKey));
		projectUserConfig = new ProjectUserConfigWidget();
		pages.addPage(new Page(projectUserConfig, "Personal Preferences", administrationKey));
		if (project.isScrumTeamMember(user)) {
			projectAdmin = new ProjectAdminWidget();
			pages.addPage(new Page(projectAdmin, "Project administration", administrationKey));
		}
		if (user.isAdmin()) {
			systemMessageManager = new SystemMessageManagerWidget();
			pages.addPage(new Page(systemMessageManager, "System Message", administrationKey));
			systemConfig = new SystemConfigWidget();
			pages.addPage(new Page(systemConfig, "System Configuration", administrationKey));
			userList = new UserListWidget();
			pages.addPage(new Page(userList, "User Management", administrationKey));
		}

		ScrumNavigatorWidget navigator = getSidebar().getNavigator();
		navigator.addItem("Dashboard", dashboard);
		addNavigatorGroup(navigator, sprintGroupKey, "Sprint");
		addNavigatorGroup(navigator, productGroupKey, "Product");
		addNavigatorGroup(navigator, projectGroupKey, "Project");
		addNavigatorGroup(navigator, collaborationGroupKey, "Collaboration");
		addNavigatorGroup(navigator, administrationKey, "Administration");
	}

	private void addNavigatorGroup(ScrumNavigatorWidget navigator, String groupKey, String label) {
		navigator.addGroup(label, groupKey);
		for (Page page : pages.getPagesByGroupKey(groupKey)) {
			navigator.addItem(groupKey, page.getLabel(), page.getWidget());
		}
	}

	@Override
	public void onProjectDataReceived(ProjectDataReceivedEvent event) {
		Scope.get().getComponent(Ui.class).show(sidebar, dashboard);
	}

	public void highlightUser(User user) {
		if (highlightedUser == user) return;
		Widget currentWidget = getWorkarea().getCurrentWidget();
		if (currentWidget instanceof UserHighlightSupport) {
			((UserHighlightSupport) currentWidget).highlightUser(user);
		}
		highlightedUser = user;
	}

	public ProjectUserConfigWidget getProjectUserConfig() {
		return projectUserConfig;
	}

	public void showEntityByReference(final String reference) {
		log.debug("Showing entity by reference:", reference);

		AGwtEntity entity = dao.getEntityByReference(reference);
		if (entity != null) {
			showEntity(entity);
			return;
		}
		Scope.get().getComponent(Ui.class).lock("Searching for " + reference);
		new RequestEntityByReferenceServiceCall(reference).execute(new Runnable() {

			@Override
			public void run() {
				AGwtEntity entity = dao.getEntityByReference(reference);
				Ui ui = Scope.get().getComponent(Ui.class);
				if (entity == null) {
					ui.unlock();
					if (reference.length() > 4 && reference.startsWith("[[")) {
						String pageName = reference.substring(2, reference.length() - 2);
						showWiki(pageName);
					} else {
						Scope.get().getComponent(Chat.class)
								.postSystemMessage("Object does not exist: " + reference, false);
					}
					return;
				}
				ui.unlock();
				showEntity(entity);
			}
		});
	}

	public void showEntityById(final String entityId) {
		log.debug("Showing entity by id:", entityId);

		AGwtEntity entity;
		try {
			entity = dao.getEntity(entityId);
		} catch (EntityDoesNotExistException ex) {
			entity = null;
		}
		if (entity != null) {
			showEntity(entity);
			return;
		}
		Scope.get().getComponent(Ui.class).lock("Searching for " + entityId);
		new RequestEntityServiceCall(entityId).execute(new Runnable() {

			@Override
			public void run() {
				AGwtEntity entity;
				try {
					entity = dao.getEntity(entityId);
				} catch (EntityDoesNotExistException ex) {
					entity = null;
				}
				Ui ui = Scope.get().getComponent(Ui.class);
				if (entity == null) {
					ui.unlock();
					Scope.get().getComponent(Chat.class).postSystemMessage("Entity does not exist: " + entityId, false);
					return;
				}
				ui.unlock();
				showEntity(entity);
			}
		});
	}

	public void showEntity(AGwtEntity entity) {
		log.debug("Showing entity:", entity);
		if (entity instanceof Task) {
			showTask((Task) entity);
		} else if (entity instanceof Requirement) {
			showRequirement((Requirement) entity);
		} else if (entity instanceof Issue) {
			showIssue((Issue) entity);
		} else if (entity instanceof Risk) {
			showRisk((Risk) entity);
		} else if (entity instanceof Quality) {
			showQualityBacklog((Quality) entity);
		} else if (entity instanceof Subject) {
			showForum((Subject) entity);
		} else if (entity instanceof Impediment) {
			showImpediment((Impediment) entity);
		} else if (entity instanceof File) {
			showFile((File) entity);
		} else if (entity instanceof Sprint) {
			showSprint((Sprint) entity);
		} else if (entity instanceof Wikipage) {
			showWiki((Wikipage) entity);
		} else if (entity instanceof SimpleEvent) {
			showCalendar((SimpleEvent) entity);
		} else if (entity instanceof Project) {
			showDashboard();
		} else if (entity instanceof ProjectEvent) {
			showProjectEventList((ProjectEvent) entity);
		} else if (entity instanceof Release) {
			showRelease((Release) entity);
		} else if (entity instanceof BlogEntry) {
			showBlog((BlogEntry) entity);
		} else if (entity instanceof User) {
			showUserList((User) entity);
		} else {
			throw new RuntimeException("Showing entity not supported: " + entity.getClass().getName());
		}
	}

	public String getPageForEntity(String entityId) {
		try {
			if (ScrumGwt.isEntityReferenceOrWikiPage(entityId))
				return getPageForEntity(dao.getEntityByReference(entityId));
			return getPageForEntity(dao.getEntityByReference(entityId));
		} catch (EntityDoesNotExistException ex) {
			return null;
		}
	}

	public String getPageForEntity(AGwtEntity entity) {
		// TODO only for subjects and entities with comments
		if (getWorkarea().isShowing(forum)) return Page.getPageName(forum);

		if (entity instanceof Task) {
			if (getWorkarea().isShowing(whiteboard)) return Page.getPageName(whiteboard);
			return Page.getPageName(sprintBacklog);
		}
		if (entity instanceof Requirement) {
			Requirement requirement = (Requirement) entity;
			boolean inCurrentSprint = requirement.isInCurrentSprint();
			if (inCurrentSprint) {
				if (getWorkarea().isShowing(sprintBacklog)) return Page.getPageName(sprintBacklog);
				return Page.getPageName(whiteboard);
			}
			return Page.getPageName(productBacklog);
		}
		if (entity instanceof Sprint) {
			Sprint sprint = (Sprint) entity;
			if (sprint.isCurrent()) return Page.getPageName(whiteboard);
			return Page.getPageName(sprintHistory);
		}
		if (entity instanceof Issue) return Page.getPageName(issueList);
		if (entity instanceof Risk) return Page.getPageName(riskList);
		if (entity instanceof Quality) return Page.getPageName(qualityBacklog);
		if (entity instanceof Subject) return Page.getPageName(forum);
		if (entity instanceof Impediment) return Page.getPageName(impedimentList);
		if (entity instanceof File) return Page.getPageName(fileRepository);
		if (entity instanceof Wikipage) return Page.getPageName(wiki);
		if (entity instanceof SimpleEvent) return Page.getPageName(calendar);
		if (entity instanceof Project) return Page.getPageName(dashboard);
		if (entity instanceof ProjectEvent) return Page.getPageName(projectEventList);
		if (entity instanceof Release) return Page.getPageName(releaseList);
		if (entity instanceof BlogEntry) return Page.getPageName(blog);
		if (entity instanceof User) return Page.getPageName(userList);
		return null;
	}

	public void showPage(String pageName) {
		Page page = pages.getPageByName(pageName);
		if (page == null) {
			if (pageName.equals(Page.getPageName(SearchResultsWidget.class))) {
				ScrumNavigatorWidget navigator = getSidebar().getNavigator();
				SearchResultsWidget results = Scope.get().getComponent(Search.class).getResultsWidget();
				if (!searchResultsAdded) {
					navigator.addItem("Search Results", results);
					searchResultsAdded = true;
				}
				select(results);
			} else {
				log.warn("Page does not exist:", pageName);
			}
			return;
		}
		select(page.getWidget());
	}

	public void showDashboard() {
		select(dashboard);
	}

	private void showSprint(Sprint sprint) {
		if (sprint.isCurrent()) {
			showSprintBacklog((Requirement) null);
		} else {
			showSprintHistory(sprint);
		}
	}

	private void showProjectEventList(ProjectEvent event) {
		select(projectEventList);
		projectEventList.select(event);
	}

	private void showSprintHistory(Sprint sprint) {
		select(sprintHistory);
		sprintHistory.select(sprint);
	}

	private void showIssue(Issue issue) {
		select(issueList);
		issueList.select(issue);
	}

	private void showRelease(Release release) {
		select(releaseList);
		releaseList.select(release);
	}

	private void showImpediment(Impediment impediment) {
		select(impedimentList);
		impedimentList.select(impediment);
	}

	private void showFile(File file) {
		select(fileRepository);
		fileRepository.select(file);
	}

	private void showRisk(Risk risk) {
		select(riskList);
		riskList.select(risk);
	}

	private void showTask(Task task) {
		if (getWorkarea().isShowing(whiteboard)) {
			showWhiteboard(task);
		} else {
			showSprintBacklog(task);
		}
	}

	private void showRequirement(Requirement requirement) {
		boolean inCurrentSprint = requirement.isInCurrentSprint();
		if (inCurrentSprint) {
			if (getWorkarea().isShowing(productBacklog)) {
				showProductBacklog(requirement);
			} else if (getWorkarea().isShowing(whiteboard)) {
				showWhiteboard(requirement);
			} else {
				showSprintBacklog(requirement);
			}
		} else {
			showProductBacklog(requirement);
		}
	}

	private void showWiki(String page) {
		select(wiki);
		if (page != null) wiki.showPage(page);
	}

	private void showWiki(Wikipage page) {
		select(wiki);
		if (page != null) wiki.showPage(page);
	}

	private SwitcherWidget getWorkarea() {
		return Scope.get().getComponent(Ui.class).getWorkspace().getWorkarea();
	}

	private void showWhiteboard(Task task) {
		select(whiteboard);
		whiteboard.selectTask(task);
	}

	private void showWhiteboard(Requirement requirement) {
		select(whiteboard);
		whiteboard.selectRequirement(requirement);
	}

	private void showSprintBacklog(Task task) {
		select(sprintBacklog);
		sprintBacklog.selectTask(task);
	}

	public void showSprintBacklog(Requirement requirement) {
		select(sprintBacklog);
		if (requirement != null) sprintBacklog.selectRequirement(requirement);
	}

	private void showProductBacklog(Requirement requirement) {
		select(productBacklog);
		productBacklog.select(requirement);
	}

	public void showForum(ForumSupport entity) {
		select(forum);
		forum.select(entity);
	}

	private void showUserList(User user) {
		select(userList);
		userList.select(user);
	}

	private void showQualityBacklog(Quality quality) {
		select(qualityBacklog);
		qualityBacklog.select(quality);
	}

	private void showBlog(BlogEntry blogEntry) {
		select(blog);
		blog.select(blogEntry);
	}

	private void showCalendar(SimpleEvent event) {
		select(calendar);
		calendar.showEvent(event);
	}

	private void select(AWidget widget) {
		getSidebar().getNavigator().select(widget);
		getWorkarea().show(widget);
		Scope.get().getComponent(Ui.class).unlock();
	}

	public WikiWidget getWiki() {
		return wiki;
	}

	public SprintHistoryWidget getSprintHistory() {
		return sprintHistory;
	}

	public CalendarWidget getCalendar() {
		return calendar;
	}

	public JournalWidget getProjectEventList() {
		return projectEventList;
	}

	public ImpedimentListWidget getImpedimentList() {
		return impedimentList;
	}

	public FileRepositoryWidget getFileRepository() {
		return fileRepository;
	}

	public IssueManagementWidget getIssueList() {
		return issueList;
	}

	public NextSprintWidget getNextSprint() {
		return nextSprint;
	}

	public ProductBacklogWidget getProductBacklog() {
		return productBacklog;
	}

	public ProjectOverviewWidget getProjectOverview() {
		return projectOverview;
	}

	public QualityBacklogWidget getQualityBacklog() {
		return qualityBacklog;
	}

	public ForumWidget getForum() {
		return forum;
	}

	public RiskListWidget getRiskList() {
		return riskList;
	}

	public ProjectSidebarWidget getSidebar() {
		return sidebar;
	}

	public SprintBacklogWidget getSprintBacklog() {
		return sprintBacklog;
	}

	public WhiteboardWidget getWhiteboard() {
		return whiteboard;
	}

}
