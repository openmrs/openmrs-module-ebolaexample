<div class="long-info-section">

    <div class="info-header">
        <i class="icon-medkit"></i>

        <h3>Today's Prescriptions</h3>
    </div>

    <div class="info-body">
        <% if (todaysDrugOrders && todaysDrugOrders.size() > 0) { %>
        <ul>
            <% todaysDrugOrders.each { %>
            <li class="clear">
                <span class="left">${ui.format(it.concept)}</span>
                <span class="right recent-lozenge">Today</span><br>
                <em>${it.drug.name}
                    ${ui.format(it.route)} - ${it.duration} Days - ${it.dose} - ${it.dosingInstructions}</em>
                <% if (it.asNeededCondition != null) { %>
                <br>
                In case of ${it.asNeededCondition}
                <% } %>
            </li>
            <% } %>
        </ul>
        <% } else { %>
        ${ui.message("coreapps.none")}
        <% } %>
    </div>
</div>
