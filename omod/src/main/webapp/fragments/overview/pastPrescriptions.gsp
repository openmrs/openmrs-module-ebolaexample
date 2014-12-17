<div class="long-info-section">

    <div class="info-header">
        <i class="icon-medkit"></i>

        <h3>Past Prescriptions</h3>
    </div>

    <div class="info-body">
        <% if (pastDrugOrders && pastDrugOrders.size() > 0) { %>
        <ul>
            <% pastDrugOrders.each { %>
            <li class="clear">
                <span class="left">${it.concept.displayString}</span>
                <span class="right recent-lozenge">${ it.dateCreated.format('dd MMM yyyy')}</span><br>
                <em>${it.drug.name}
                    ${it.route.displayString} - ${it.duration} Days - ${it.dose} - ${it.dosingInstructions}</em>
            </li>
            <% } %>
        </ul>
        <% } else { %>
        ${ui.message("coreapps.none")}
        <% } %>
    </div>
</div>